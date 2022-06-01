package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Tab;
import javafx.stage.FileChooser;
import org.example.brake.BrakeController;
import org.example.calibrate.CalibrateController;
import org.example.clutch.ClutchController;
import org.example.overlay.OverlayController;
import org.example.theme.ThemeController;
import org.example.throttle.ThrottleController;
import org.example.time.TimeController;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrimaryController {
    String deviceName = "PedalBox";

    public SerialPort comPort;

    // set inital boolean observable list auto connect
    BooleanProperty isSerialDeviceFound = new SimpleBooleanProperty(false);

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
    private ThemeController themeController;


    @FXML
    private Tab tab_calibration;

    @FXML
    private Tab tab_pedals;

    @FXML
    void initialize() {

        tab_pedals.textProperty().bind(UserStorageAndConfiguration.createStringBinding("tab.pedals.title"));
        tab_calibration.textProperty().bind(UserStorageAndConfiguration.createStringBinding("tab.calibration.title"));

        themeController.injectMainController(this);
        throttleController.injectMainController(this);
        clutchController.injectMainController(this);
        brakeController.injectMainController(this);
        overlayController.injectMainController(this);
        timeController.injectMainController(this);
        calibrateController.injectMainController(this);

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

        isSerialDeviceFound.addListener((obs, oldIsDeviceFound, newIsDeviceFound) -> {

            if (newIsDeviceFound) {
//                System.out.println("found Device");
                comPort = findSerialPort(deviceName, Arrays.asList(SerialPort.getCommPorts()));
                // allow other controllers to access serial com port
                comPort.setComPortTimeouts(0, 0, 0);
                comPort.openPort();

                overlayController.hideOverlay();

                // get bits map
                writeSerial("GetBits\n");
                // get inverted map
                writeSerial("GetInverted\n");
                // get smooth map
                writeSerial("GetSmooth\n");
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
                                        PedalInverted(cleanString);
                                        PedalSmooth(cleanString);
                                        PedalBits(cleanString);
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

    private Map<String, Long> splitPedalInputToMap(String items, String toReplace) {
        Map<String, Long> map = new HashMap<String, Long>();
        String replaced = items.replaceAll(toReplace, "");
        String[] splitItems = replaced.split(";");

        map.put("after",Long.parseLong(splitItems[0]));
        map.put("before", Long.parseLong(splitItems[1]));
        map.put("raw", Long.parseLong(splitItems[2]));
        map.put("hid", Long.parseLong(splitItems[3]));

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
        System.out.println(textLine + " writeSerial");
        byte[] buffer2 = textLine.getBytes();
        comPort.writeBytes(buffer2, buffer2.length);
    }

    private void PedalCalibration(String cleanString) {
        Pattern patternPedalCalibration = Pattern.compile("(TCALI:([\\d\\-\\n]+),BCALI:([\\d\\-\\n]+),CCALI:([\\d\\-\\n]+))", Pattern.CASE_INSENSITIVE);
        Matcher matcherPedalCalibration = patternPedalCalibration.matcher(cleanString);
        boolean matchFoundPedalCalibration = matcherPedalCalibration.find();

        if (matchFoundPedalCalibration) {
            System.out.println(cleanString);
            String[] splitPedalCalibration = cleanString.split(",");

            long[] ThrottleCalibration = Arrays.stream(splitPedalCalibration[0].replaceAll("TCALI:", "").split("-")).mapToLong(Long::parseLong).toArray();
            long[] BrakeCalibration = Arrays.stream(splitPedalCalibration[1].replaceAll("BCALI:", "").split("-")).mapToLong(Long::parseLong).toArray();
            long[] ClutchCalibration = Arrays.stream(splitPedalCalibration[2].replaceAll("CCALI:", "").split("-")).mapToLong(Long::parseLong).toArray();

            calibrateController.setCalibration(ThrottleCalibration, BrakeCalibration, ClutchCalibration);
        }
    }

    private void PedalMap(String cleanString) {
        Pattern patternPedalMap = Pattern.compile("(TMAP:([\\d\\-\\n]+),BMAP:([\\d\\-\\n]+),CMAP:([\\d\\-\\n]+))", Pattern.CASE_INSENSITIVE);
        Matcher matcherPedalMap = patternPedalMap.matcher(cleanString);
        boolean matchFoundPedalMap = matcherPedalMap.find();

        if (matchFoundPedalMap) {
            System.out.println(cleanString);
            String[] splitPedalMap = cleanString.split(",");

            int[] ThrottleMap = Arrays.stream(splitPedalMap[0].replaceAll("TMAP:", "").split("-")).mapToInt(Integer::parseInt).toArray();
            int[] BrakeMap = Arrays.stream(splitPedalMap[1].replaceAll("BMAP:", "").split("-")).mapToInt(Integer::parseInt).toArray();
            int[] ClutchMap = Arrays.stream(splitPedalMap[2].replaceAll("CMAP:", "").split("-")).mapToInt(Integer::parseInt).toArray();

            throttleController.setThrottleMap(ThrottleMap);
            brakeController.setBrakeMap(BrakeMap);
            clutchController.setClutchMap(ClutchMap);
        }
    }

    private void PedalInverted(String cleanString) {
        Pattern patternPedalInverted = Pattern.compile("(INVER:([\\d\\-\\n]+))", Pattern.CASE_INSENSITIVE);
        Matcher matcherPedalInverted = patternPedalInverted.matcher(cleanString);
        boolean matchFoundPedalInverted = matcherPedalInverted.find();
        if (matchFoundPedalInverted) {
            System.out.println(cleanString);
            String[] splitPedalInverted = cleanString.replaceAll("INVER:", "").split("-");
            throttleController.setInverted(splitPedalInverted[0]);
            brakeController.setInverted(splitPedalInverted[1]);
            clutchController.setInverted(splitPedalInverted[2]);
        }
    }

    private void PedalSmooth(String cleanString) {
        Pattern patternPedalSmooth = Pattern.compile("(SMOOTH:([\\d\\-\\n]+))", Pattern.CASE_INSENSITIVE);
        Matcher matcherPedalSmooth = patternPedalSmooth.matcher(cleanString);
        boolean matchFoundPedalSmooth = matcherPedalSmooth.find();
        if (matchFoundPedalSmooth) {
            System.out.println(cleanString);
            String[] splitPedalSmooth = cleanString.replaceAll("SMOOTH:", "").split("-");
            throttleController.setSmooth(splitPedalSmooth[0]);
            brakeController.setSmooth(splitPedalSmooth[1]);
            clutchController.setSmooth(splitPedalSmooth[2]);
        }
    }
    private void PedalBits(String cleanString) {
        Pattern patternPedalBits = Pattern.compile("(BITS:([\\d\\-\\n]+))", Pattern.CASE_INSENSITIVE);
        Matcher matcherPedalBits = patternPedalBits.matcher(cleanString);
        boolean matchFoundPedalBits = matcherPedalBits.find();
        if (matchFoundPedalBits) {
            System.out.println(cleanString);
            String[] splitPedalBits = cleanString.replaceAll("BITS:", "").split("-");
            calibrateController.setBits(
                    Long.parseLong(splitPedalBits[0]),
                    Long.parseLong(splitPedalBits[1]),
                    Long.parseLong(splitPedalBits[2]),
                    Long.parseLong(splitPedalBits[3]),
                    Long.parseLong(splitPedalBits[4]),
                    Long.parseLong(splitPedalBits[5])
            );
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
                Map<String, Long> ThrottleValues = splitPedalInputToMap(splitPedalInput[0], "T:");
                Map<String, Long> BrakeValues = splitPedalInputToMap(splitPedalInput[1], "B:");
                Map<String, Long> ClutchValues = splitPedalInputToMap(splitPedalInput[2], "C:");

                clutchController.setClutchPosition(ClutchValues);
                brakeController.setBrakePosition(BrakeValues);
                throttleController.setThrottlePosition(ThrottleValues);

                calibrateController.setClutchPositionRaw(ClutchValues);
                calibrateController.setBrakePositionRaw(BrakeValues);
                calibrateController.setThrottlePositionRaw(ThrottleValues);

                timeController.setClutchPosition(ClutchValues);
                timeController.setBrakePosition(BrakeValues);
                timeController.setThrottlePosition(ThrottleValues);
            }
        }
    }

    public String invertedSettings(String throttle, String brake, String clutch) {
        return "INVER:" + throttle + "-" + brake + "-" + clutch + "\n";
    }

    public String smoothSettings(String throttle, String brake, String clutch) {
        return "SMOOTH:" + throttle + "-" + brake + "-" + clutch + "\n";
    }

    public void handleSaveToArduino(ActionEvent actionEvent) {
        writeSerial("TMAP:" + throttleController.saveTMAPSettings() + "\n");
        writeSerial("BMAP:" + brakeController.saveBMAPSettings() + "\n");
        writeSerial("CMAP:" + clutchController.saveCMAPSettings() + "\n");

        writeSerial(
                invertedSettings(
                        throttleController.saveInvertedSettings(),
                        brakeController.saveInvertedSettings(),
                        clutchController.saveInvertedSettings()
                )
        );

        writeSerial(
                smoothSettings(
                        throttleController.saveSmoothSettings(),
                        brakeController.saveSmoothSettings(),
                        clutchController.saveSmoothSettings()
                )
        );
    }

    public void handleSavePreferences(ActionEvent actionEvent) {
        System.out.println("handleSavePreferences");
        final FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Save JSON File");
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        File selectedFile = fileChooser.showSaveDialog(null);

        if (selectedFile != null) {
            try {

                PedalPOJO json = new PedalPOJO();

                int throttleInverted = Integer.parseInt(throttleController.saveInvertedSettings());
                int brakeInverted = Integer.parseInt(brakeController.saveInvertedSettings());
                int clutchInverted = Integer.parseInt(clutchController.saveInvertedSettings());
                int[] inverted = {throttleInverted, brakeInverted, clutchInverted};
                json.setInverted(inverted);

                int[] ThrottleMap = Arrays.stream(throttleController.saveTMAPSettings().split("-")).mapToInt(Integer::parseInt).toArray();
                json.setThrottle(ThrottleMap);

                int[] BrakeMap = Arrays.stream(brakeController.saveBMAPSettings().split("-")).mapToInt(Integer::parseInt).toArray();
                json.setBrake(BrakeMap);

                int[] ClutchMap = Arrays.stream(clutchController.saveCMAPSettings().split("-")).mapToInt(Integer::parseInt).toArray();
                json.setClutch(ClutchMap);


                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(Paths.get(selectedFile.getAbsolutePath()).toFile(), json);

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("file loading error");
            }
        }
    }

    public void handleLoadPreferences(ActionEvent actionEvent) {
        System.out.println("handleLoadPreferences");

        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load JSON File");
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                PedalPOJO JSONtoJava = mapper.readValue(new File(selectedFile.getAbsolutePath()), PedalPOJO.class);
                String JsonPrettyPrint = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(JSONtoJava);
                System.out.println(JsonPrettyPrint);

                int[] inverted = JSONtoJava.getInverted();
                throttleController.setInverted(String.valueOf(inverted[0]));
                brakeController.setInverted(String.valueOf(inverted[1]));
                clutchController.setInverted(String.valueOf(inverted[2]));

                throttleController.setThrottleMap(JSONtoJava.getThrottle());
                brakeController.setBrakeMap(JSONtoJava.getBrake());
                clutchController.setClutchMap(JSONtoJava.getClutch());

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("file loading error");
            }
        }

    }

}
