package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Group;
import org.example.brake.BrakeController;
import org.example.clutch.ClutchController;
import org.example.overlay.OverlayController;
import org.example.calibrate.CalibrateController;
import org.example.services.httpservice.HttpService;
import org.example.services.websocketservice.WebsocketService;
import org.example.throttle.ThrottleController;
import org.example.time.TimeController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrimaryController {
//    Scene scene = App.getScene();

    public SerialPort serialPortConnection;

    public HttpService httpService;

    public WebsocketService websocketService;


    String deviceName = "PedalBox";
//    String deviceName = "CP210";

    @FXML
    public Group calibrate_group;

    @FXML
    private Group clutch_group;

    @FXML
    private Group brake_group;

    @FXML
    private Group throttle_group;

    @FXML
    public Group time_group;

    @FXML
    private ThrottleController throttleController;

    @FXML
    private ClutchController clutchController;

    @FXML
    private BrakeController brakeController;

    @FXML
    private OverlayController overlayController;

    @FXML
    private TimeController timeController;

    @FXML
    private CalibrateController calibrateController;

    @FXML
    void initialize() {

        new Thread(() -> {
            try {
                httpService = new HttpService(3000, 0);
                httpService.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }, "httpService").start();

        new Thread(() -> {
            try {
                websocketService = new WebsocketService();
                websocketService.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "websocketService").start();

        throttleController.injectMainController(this);
        clutchController.injectMainController(this);
        brakeController.injectMainController(this);
        overlayController.injectMainController(this);
        timeController.injectMainController(this);
        calibrateController.injectMainController(this);


        // set inital boolean observable list auto connect
        BooleanProperty isSerialDeviceFound = new SimpleBooleanProperty(false);

        Timer SerialPollerTimer = new Timer();
        TimerTask SerialPollerTask = new TimerTask() {
            @Override
            public void run() {
                Boolean isDeviceFound = isSerialPortFound(deviceName, Arrays.asList(SerialPort.getCommPorts()));
                // change observable value
                isSerialDeviceFound.set(isDeviceFound);
            }
        };
        SerialPollerTimer.schedule(SerialPollerTask, 100, 1000);

        // set initial overlay to visible
        overlayController.showOverlay();

        isSerialDeviceFound.addListener((obs, oldValue, newValue) -> {

//            System.out.printf("isSerialDeviceFound value changed from %s to %s%n", oldValue, newValue);

            if (isSerialPortFound(deviceName, Arrays.asList(SerialPort.getCommPorts()))) {
//                System.out.println("found Device");
                SerialPort comPort = findSerialPort(deviceName, Arrays.asList(SerialPort.getCommPorts()));
                // allow other controllers to access serial com port
                this.serialPortConnection = comPort;
                comPort.setComPortTimeouts(0, 0, 0);
                comPort.openPort();

                overlayController.hideOverlay();

                // get pedal map
                writeSerial("GetMap\n");
                // get calibration map
                writeSerial("GetCali\n");

                comPort.addDataListener(new SerialPortDataListener() {
                    String buffer = "";

                    @Override
                    public int getListeningEvents() {
                        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
                    }

                    @Override
                    public void serialEvent(SerialPortEvent event) {
                        Platform.runLater(() -> {
                            if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                                String line;
                                BufferedReader br = null;
                                try {
                                    br = new BufferedReader(new InputStreamReader(comPort.getInputStream(), "UTF-8"));
                                    while ((line = br.readLine()) != null) {
//                                    System.out.println(line);
                                        String cleanString = line.replaceAll("\r", "").replaceAll("\n", "");
                                        PedalInput(cleanString);
                                        PedalMap(cleanString);
                                        PedalCalibration(cleanString);
                                    }
                                } catch (IOException e) {
//                                e.printStackTrace();
                                } finally {
                                    try {
                                        br.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });
                    }
                });
            } else {
                System.out.println("Serial device not found!!");
                overlayController.showOverlay();
            }
        });


    }



    public static boolean isSerialPortFound(String name, List<SerialPort> SerialPorts) {
        for (SerialPort SerialPort : SerialPorts) {
            if (SerialPort.toString().contains(name)) {
                return true;
            }
        }
        return false;
    }

    private Map<String, Integer> splitPedalInputToMap(String items, String toReplace) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        String replaced = items.replaceAll(toReplace, "");
        String[] splitItems = replaced.split(";");

//        try {
        map.put("after", Math.round(Float.parseFloat(splitItems[0])));
        map.put("before", Math.round(Float.parseFloat(splitItems[1])));
        map.put("raw", Math.round(Float.parseFloat(splitItems[2])));
//        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
//            map.put("after", 0);
//            map.put("before", 0);
//        }

        return map;
    }

    public static SerialPort findSerialPort(String name, List<SerialPort> SerialPorts) {
        for (SerialPort SerialPort : SerialPorts) {
            if (SerialPort.toString().contains(name)) {
                return SerialPort;
            }
        }
        return null;
    }

    public void writeSerial(String textLine) {
        byte[] buffer2 = textLine.getBytes();
        this.serialPortConnection.writeBytes(buffer2, buffer2.length);
    }

    private void PedalCalibration(String cleanString) {
        Pattern patternPedalCalibration = Pattern.compile("(TCALI:([\\d\\-\\n]+),BCALI:([\\d\\-\\n]+),CCALI:([\\d\\-\\n]+))", Pattern.CASE_INSENSITIVE);
        Matcher matcherPedalCalibration = patternPedalCalibration.matcher(cleanString);
        boolean matchFoundPedalCalibration = matcherPedalCalibration.find();

        if (matchFoundPedalCalibration) {
            String[] splitPedalCalibration = cleanString.split(",");

            int[] ThrottleCalibration = Arrays.stream(splitPedalCalibration[0].replaceAll("TCALI:", "").split("-")).mapToInt(Integer::parseInt).toArray();
            int[] BrakeCalibration = Arrays.stream(splitPedalCalibration[1].replaceAll("BCALI:", "").split("-")).mapToInt(Integer::parseInt).toArray();
            int[] ClutchCalibration = Arrays.stream(splitPedalCalibration[2].replaceAll("CCALI:", "").split("-")).mapToInt(Integer::parseInt).toArray();

            calibrateController.setCalibration(ClutchCalibration, BrakeCalibration, ThrottleCalibration);
        }
    }

    private void PedalMap(String cleanString) {
        Pattern patternPedalMap = Pattern.compile("(TMAP:([\\d\\-\\n]+),BMAP:([\\d\\-\\n]+),CMAP:([\\d\\-\\n]+))", Pattern.CASE_INSENSITIVE);
        Matcher matcherPedalMap = patternPedalMap.matcher(cleanString);
        boolean matchFoundPedalMap = matcherPedalMap.find();

        if (matchFoundPedalMap) {
            String[] splitPedalMap = cleanString.split(",");

            int[] ThrottleMap = Arrays.stream(splitPedalMap[0].replaceAll("TMAP:", "").split("-")).mapToInt(Integer::parseInt).toArray();
            int[] BrakeMap = Arrays.stream(splitPedalMap[1].replaceAll("BMAP:", "").split("-")).mapToInt(Integer::parseInt).toArray();
            int[] ClutchMap = Arrays.stream(splitPedalMap[2].replaceAll("CMAP:", "").split("-")).mapToInt(Integer::parseInt).toArray();

            throttleController.setThrottleMap(ThrottleMap);
            brakeController.setBrakeMap(BrakeMap);
            clutchController.setClutchMap(ClutchMap);
        }
    }

    private void PedalInput(String cleanString) {

        //T:0;0,B:0;0,C:0;0,
        //T:0;0;0,B:0;0;0,C:0;0;0,
        Pattern patternPedalInput = Pattern.compile("(T:((\\d+\\.\\d+|\\d+)+[;,])+)(B:((\\d+\\.\\d+|\\d+)+[;,])+)(C:((\\d+\\.\\d+|\\d+)+[;,])+)", Pattern.CASE_INSENSITIVE);
        Matcher matcherPedalInput = patternPedalInput.matcher(cleanString);
        boolean matchFoundPedalInput = matcherPedalInput.find();

        if (matchFoundPedalInput) {
            String[] splitPedalInput = cleanString.split(",");
            if (splitPedalInput.length > 2) {
                Map<String, Integer> ThrottleValues = splitPedalInputToMap(splitPedalInput[0], "T:");
                Map<String, Integer> BrakeValues = splitPedalInputToMap(splitPedalInput[1], "B:");
                Map<String, Integer> ClutchValues = splitPedalInputToMap(splitPedalInput[2], "C:");

                clutchController.setClutchPosition(ClutchValues);
                brakeController.setBrakePosition(BrakeValues);
                throttleController.setThrottlePosition(ThrottleValues);

                calibrateController.setClutchPositionRaw(ClutchValues);
                calibrateController.setBrakePositionRaw(BrakeValues);
                calibrateController.setThrottlePositionRaw(ThrottleValues);

                timeController.setClutchPosition(ClutchValues);
                timeController.setBrakePosition(BrakeValues);
                timeController.setThrottlePosition(ThrottleValues);

                sendPedalInputToWebsockets(ThrottleValues, BrakeValues, ClutchValues);
            }
        }
    }

    private void sendPedalInputToWebsockets(Map<String, Integer> ThrottleValues, Map<String, Integer> BrakeValues, Map<String, Integer> ClutchValues) {
        if (websocketService.hasClients()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode pedalJSON = mapper.createObjectNode();

                ObjectNode throttle = mapper.createObjectNode();
                throttle.put("before", ThrottleValues.get("before"));
                throttle.put("after", ThrottleValues.get("after"));
                pedalJSON.set("throttle", throttle);

                ObjectNode brake = mapper.createObjectNode();
                brake.put("before", BrakeValues.get("before"));
                brake.put("after", BrakeValues.get("after"));
                pedalJSON.set("brake", brake);

                ObjectNode clutch = mapper.createObjectNode();
                clutch.put("before", ClutchValues.get("before"));
                clutch.put("after", ClutchValues.get("after"));
                pedalJSON.set("clutch", clutch);

                String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(pedalJSON);
                websocketService.broadcast(json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

}
