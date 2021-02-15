package org.example.calibrate;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.PrimaryController;

import java.util.HashMap;
import java.util.Map;

public class CalibrateController {
    private PrimaryController controller;

    @FXML
    public Button resetCalibrationButton;

    @FXML
    public Button saveCalibrationButton;


    private Map<String, Integer> calibrationValues = new HashMap<String, Integer>();

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
        calibrationValues.put("ClutchHigh",CalibratedClutch.get("high"));
        calibrationValues.put("ClutchLow",CalibratedClutch.get("low"));
    }

    public void reportBrakeCalibration(Map<String, Integer> CalibratedBrake){
        calibrationValues.put("BrakeHigh",CalibratedBrake.get("high"));
        calibrationValues.put("BrakeLow",CalibratedBrake.get("low"));
    }


    public void reportThrottleCalibration(Map<String, Integer> CalibratedThrottle){
        calibrationValues.put("ThrottleHigh",CalibratedThrottle.get("high"));
        calibrationValues.put("ThrottleLow",CalibratedThrottle.get("low"));
    }

    public void initialize() {
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
        String CCALI = "CCALI:" + calibrationValues.get("ClutchLow") + "-" + calibrationValues.get("ClutchHigh");
        String BCALI = "BCALI:" + calibrationValues.get("BrakeLow") + "-" + calibrationValues.get("BrakeHigh");
        String TCALI = "TCALI:" + calibrationValues.get("ThrottleLow") + "-" + calibrationValues.get("ThrottleHigh");

        String textLine = CCALI + "," + BCALI + "," + TCALI;
        this.controller.writeSerial(textLine);
    }


    private void printMap(Map<String, Integer> calibrationValues){
        calibrationValues.forEach((key, value) -> System.out.println(key + ":" + value));
    }

    public void setCalibration(int[] clutchCalibration, int[] brakeCalibration, int[] throttleCalibration) {
        calibrationValues.put("ClutchLow",clutchCalibration[0]);
        calibrationValues.put("ClutchHigh",clutchCalibration[1]);
        calibrationValues.put("BrakeLow",brakeCalibration[0]);
        calibrationValues.put("BrakeHigh",brakeCalibration[1]);
        calibrationValues.put("ThrottleLow",throttleCalibration[0]);
        calibrationValues.put("ThrottleHigh",throttleCalibration[1]);
    }

    private void resetCalibration(){
        this.controller.writeSerial("GetCali\n");
    }
}
