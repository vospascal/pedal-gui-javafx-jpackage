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
import org.example.throttle.ThrottleController;
import org.example.time.TimeController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrimaryController {
    public SerialPort serialPortConnection;

    public CustomWebSocket customWebSocket;

    public HttpService httpService;

    @FXML
    private Group clutch_group;

    @FXML
    private Group brake_group;

    @FXML
    private Group throttle_group;

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
    void initialize() {

        try {
            customWebSocket = new CustomWebSocket(3001);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            httpService = new HttpService(3000, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        customWebSocket.start();
        httpService.start();

        throttleController.injectMainController(this);
        clutchController.injectMainController(this);
        brakeController.injectMainController(this);
        overlayController.injectMainController(this);
        timeController.injectMainController(this);

        // set inital boolean observable list auto connect
        BooleanProperty isSerialPedalBoxFound = new SimpleBooleanProperty(false);

        Timer SerialPollerTimer = new Timer();
        TimerTask SerialPollerTask = new TimerTask() {
            @Override
            public void run() {
                Boolean isPedalBoxFound = isSerialPortFound("PedalBox", Arrays.asList(SerialPort.getCommPorts()));
                // change observable value
                isSerialPedalBoxFound.set(isPedalBoxFound);
            }
        };
        SerialPollerTimer.schedule(SerialPollerTask, 100, 1000);

        // set initial overlay to visible
        overlayController.showOverlay();

        isSerialPedalBoxFound.addListener((obs, oldValue, newValue) -> {
//            System.out.printf("isSerialPedalBoxFound value changed from %s to %s%n", oldValue, newValue);
            if (isSerialPortFound("PedalBox", Arrays.asList(SerialPort.getCommPorts()))) {
//                System.out.println("found PedalBox");
                SerialPort comPort = findSerialPort("PedalBox", Arrays.asList(SerialPort.getCommPorts()));
                // allow other controllers to access serial com port
                this.serialPortConnection = comPort;
                comPort.setComPortTimeouts(0, 0, 0);
                comPort.openPort();

                overlayController.hideOverlay();

                writeSerial("Getmap:\n");

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
                System.out.println("PedalBox not found!!");
                overlayController.showOverlay();
            }
        });

    }

    public static boolean isSerialPortFound(String name, List<SerialPort> SerialPorts) {
        for (SerialPort SerialPort : SerialPorts) {
            if (SerialPort.toString().equals(name)) {
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
//        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
//            map.put("after", 0);
//            map.put("before", 0);
//        }

        return map;
    }

    public static SerialPort findSerialPort(String name, List<SerialPort> SerialPorts) {
        for (SerialPort SerialPort : SerialPorts) {
            if (SerialPort.toString().equals(name)) {
                return SerialPort;
            }
        }
        return null;
    }

    public void writeSerial(String textLine) {
        byte[] buffer2 = textLine.getBytes();
        this.serialPortConnection.writeBytes(buffer2, buffer2.length);
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
        Pattern patternPedalInput = Pattern.compile("(T:(\\d+\\.\\d+|\\d+);(\\d+\\.\\d+|\\d+)),(B:(\\d+\\.\\d+|\\d+);(\\d+\\.\\d+|\\d+)),(C:(\\d+\\.\\d+|\\d+);(\\d+\\.\\d+|\\d+))", Pattern.CASE_INSENSITIVE);
        Matcher matcherPedalInput = patternPedalInput.matcher(cleanString);
        boolean matchFoundPedalInput = matcherPedalInput.find();

        if (matchFoundPedalInput) {
            String[] splitPedalInput = cleanString.split(",");
            if (splitPedalInput.length > 2) {
                Map<String, Integer> ThrottleValues = splitPedalInputToMap(splitPedalInput[0], "T:");
                Map<String, Integer> BrakeValues = splitPedalInputToMap(splitPedalInput[1], "B:");
                Map<String, Integer> ClutchValues = splitPedalInputToMap(splitPedalInput[2], "C:");

                throttleController.setThrottlePosition(ThrottleValues);
                brakeController.setBrakePosition(BrakeValues);
                clutchController.setClutchPosition(ClutchValues);

                timeController.setBrakePosition(BrakeValues);
                timeController.setClutchPosition(ClutchValues);
                timeController.setThrottlePosition(ThrottleValues);


                if(customWebSocket.getClients() != null){
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
                        customWebSocket.sndmsg(json, customWebSocket.getClients());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }


}
