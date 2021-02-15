package org.example.calibrate;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.PrimaryController;

import java.util.Map;

public class CalibrateController {
    private PrimaryController controller;

    @FXML
    public Button resetCalibrationButton;

    @FXML
    public Button saveCalibrationButton;


//    private Map<String, Integer> calibrationValues = new HashMap<String, Integer>();
    private ObservableMap<String, Integer> calibrationValues = FXCollections.observableHashMap();



    @FXML
    private CalibrateClutchController calibrateClutchController;

    @FXML
    private CalibrateBrakeController calibrateBrakeController;

    @FXML
    private CalibrateThrottleController calibrateThrottleController;

    public void injectMainController(PrimaryController primaryController) {
        this.controller = primaryController;
    }

    public void setClutchPositionRaw(Map<String, Integer> clutchValues) {
        calibrateClutchController.setValues(clutchValues);
    }

    public void setBrakePositionRaw(Map<String, Integer> brakeValues) {
        calibrateBrakeController.setValues(brakeValues);
    }

    public void setThrottlePositionRaw(Map<String, Integer> throttleValues) {
        calibrateThrottleController.setValues(throttleValues);
    }

    public void reportClutchCalibration(Map<String, Integer> CalibratedClutch){
        calibrationValues.put("ClutchCalibrationLow",CalibratedClutch.get("calibrationLow"));
        calibrationValues.put("ClutchCalibrationHigh",CalibratedClutch.get("calibrationHigh"));
        calibrationValues.put("ClutchDeadzoneLow",CalibratedClutch.get("deadzoneLow"));
        calibrationValues.put("ClutchDeadzoneHigh",CalibratedClutch.get("deadzoneHigh"));
    }

    public void reportBrakeCalibration(Map<String, Integer> CalibratedBrake){
        calibrationValues.put("BrakeCalibrationLow",CalibratedBrake.get("calibrationLow"));
        calibrationValues.put("BrakeCalibrationHigh",CalibratedBrake.get("calibrationHigh"));
        calibrationValues.put("BrakeDeadzoneLow",CalibratedBrake.get("deadzoneLow"));
        calibrationValues.put("BrakeDeadzoneHigh",CalibratedBrake.get("deadzoneHigh"));
    }


    public void reportThrottleCalibration(Map<String, Integer> CalibratedThrottle){
        calibrationValues.put("ThrottleCalibrationLow",CalibratedThrottle.get("calibrationLow"));
        calibrationValues.put("ThrottleCalibrationHigh",CalibratedThrottle.get("calibrationHigh"));
        calibrationValues.put("ThrottleDeadzoneLow",CalibratedThrottle.get("deadzoneLow"));
        calibrationValues.put("ThrottleDeadzoneHigh",CalibratedThrottle.get("deadzoneHigh"));
    }

    public void initialize() {

        calibrationValues.addListener(new MapChangeListener<String, Integer> () {
            @Override
            public void onChanged(MapChangeListener.Change<? extends String, ? extends Integer> change) {
                if (change.wasAdded()) {
                    try {
                        calibrateClutchController.setCalibrationValues(
                                calibrationValues.get("ClutchCalibrationLow"),
                                calibrationValues.get("ClutchCalibrationHigh"),
                                calibrationValues.get("ClutchDeadzoneLow"),
                                calibrationValues.get("ClutchDeadzoneHigh")
                        );

                        calibrateBrakeController.setCalibrationValues(
                                calibrationValues.get("BrakeCalibrationLow"),
                                calibrationValues.get("BrakeCalibrationHigh"),
                                calibrationValues.get("BrakeDeadzoneLow"),
                                calibrationValues.get("BrakeDeadzoneHigh")
                        );
                        calibrateThrottleController.setCalibrationValues(
                                calibrationValues.get("ThrottleCalibrationLow"),
                                calibrationValues.get("ThrottleCalibrationHigh"),
                                calibrationValues.get("ThrottleDeadzoneLow"),
                                calibrationValues.get("ThrottleDeadzoneHigh")
                        );
                    }catch (Exception e) {}
                }
                if (change.wasRemoved()) {
                    try {
                        calibrateClutchController.setCalibrationValues(
                                calibrationValues.get("ClutchCalibrationLow"),
                                calibrationValues.get("ClutchCalibrationHigh"),
                                calibrationValues.get("ClutchDeadzoneLow"),
                                calibrationValues.get("ClutchDeadzoneHigh")
                        );
                        calibrateBrakeController.setCalibrationValues(
                                calibrationValues.get("BrakeCalibrationLow"),
                                calibrationValues.get("BrakeCalibrationHigh"),
                                calibrationValues.get("BrakeDeadzoneLow"),
                                calibrationValues.get("BrakeDeadzoneHigh")
                        );
                        calibrateThrottleController.setCalibrationValues(
                                calibrationValues.get("ThrottleCalibrationLow"),
                                calibrationValues.get("ThrottleCalibrationHigh"),
                                calibrationValues.get("ThrottleDeadzoneLow"),
                                calibrationValues.get("ThrottleDeadzoneHigh")
                        );
                    }catch (Exception e) {}
                }
            }
        });



        calibrateClutchController.injectMainController(this);
        calibrateThrottleController.injectMainController(this);
        calibrateBrakeController.injectMainController(this);

        saveCalibrationButton.setOnAction((event) -> {
            printMap(calibrationValues);
            saveCalibration();
        });

        resetCalibrationButton.setOnAction((event) -> {
            printMap(calibrationValues);
            resetCalibration();
        });

    }

    private void saveCalibration(){
        String CCALI = "CCALI:" + calibrationValues.get("ClutchCalibrationLow") + "-" + calibrationValues.get("ClutchCalibrationHigh") + "-" + calibrationValues.get("ClutchDeadzoneLow") + "-" + calibrationValues.get("ClutchDeadzoneHigh");
        String BCALI = "BCALI:" + calibrationValues.get("BrakeCalibrationLow") + "-" + calibrationValues.get("BrakeCalibrationHigh")+ "-" + calibrationValues.get("BrakeDeadzoneLow") + "-" + calibrationValues.get("BrakeDeadzoneHigh");
        String TCALI = "TCALI:" + calibrationValues.get("ThrottleCalibrationLow") + "-" + calibrationValues.get("ThrottleCalibrationHigh")+ "-" + calibrationValues.get("ThrottleDeadzoneLow") + "-" + calibrationValues.get("ThrottleDeadzoneHigh");

        String textLine = CCALI + "," + BCALI + "," + TCALI;
        this.controller.writeSerial(textLine);
    }


    private void printMap(Map<String, Integer> calibrationValues){
        calibrationValues.forEach((key, value) -> System.out.println(key + ":" + value));
    }

    public void setCalibration(int[] clutchCalibration, int[] brakeCalibration, int[] throttleCalibration) {
        calibrationValues.put("ClutchCalibrationLow",clutchCalibration[0]);
        calibrationValues.put("ClutchCalibrationHigh",clutchCalibration[1]);
        calibrationValues.put("ClutchDeadzoneLow",clutchCalibration[2]);
        calibrationValues.put("ClutchDeadzoneHigh",clutchCalibration[3]);

        calibrationValues.put("BrakeCalibrationLow",brakeCalibration[0]);
        calibrationValues.put("BrakeCalibrationHigh",brakeCalibration[1]);
        calibrationValues.put("BrakeDeadzoneLow",brakeCalibration[2]);
        calibrationValues.put("BrakeDeadzoneHigh",brakeCalibration[3]);

        calibrationValues.put("ThrottleCalibrationLow",throttleCalibration[0]);
        calibrationValues.put("ThrottleCalibrationHigh",throttleCalibration[1]);
        calibrationValues.put("ThrottleDeadzoneLow",throttleCalibration[2]);
        calibrationValues.put("ThrottleDeadzoneHigh",throttleCalibration[3]);
    }

    private void resetCalibration(){
        this.controller.writeSerial("GetCali\n");
    }
}
