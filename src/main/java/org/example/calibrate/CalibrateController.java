package org.example.calibrate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.PrimaryController;

import java.util.Map;

public class CalibrateController {
    private PrimaryController controller;
    private ObservableMap<String, Long> calibrationValues = FXCollections.observableHashMap();

    @FXML
    public Button resetCalibrationButton;

    @FXML
    public Button saveCalibrationButton;

    @FXML
    private CalibrateClutchController calibrateClutchController;

    @FXML
    private CalibrateBrakeController calibrateBrakeController;

    @FXML
    private CalibrateThrottleController calibrateThrottleController;

    public void injectMainController(PrimaryController primaryController) {
        this.controller = primaryController;
    }

    public void setClutchPositionRaw(Map<String, Long> clutchValues) {
        calibrateClutchController.setValues(clutchValues);
    }

    public void reportClutchCalibration() {
        Map<String, Long> calibrated = calibrateClutchController.getCalibration();
        calibrationValues.put("ClutchCalibrationLow", calibrated.get("calibrationLow"));
        calibrationValues.put("ClutchCalibrationHigh", calibrated.get("calibrationHigh"));
        calibrationValues.put("ClutchDeadzoneLow", calibrated.get("deadzoneLow"));
        calibrationValues.put("ClutchDeadzoneHigh", calibrated.get("deadzoneHigh"));
    }

    public void setBrakePositionRaw(Map<String, Long> brakeValues) {
        calibrateBrakeController.setValues(brakeValues);
    }

    public void reportBrakeCalibration() {
        Map<String, Long> calibrated = calibrateBrakeController.getCalibration();
        calibrationValues.put("BrakeCalibrationLow", calibrated.get("calibrationLow"));
        calibrationValues.put("BrakeCalibrationHigh", calibrated.get("calibrationHigh"));
        calibrationValues.put("BrakeDeadzoneLow", calibrated.get("deadzoneLow"));
        calibrationValues.put("BrakeDeadzoneHigh", calibrated.get("deadzoneHigh"));
    }

    public void setThrottlePositionRaw(Map<String, Long> throttleValues) {
        calibrateThrottleController.setValues(throttleValues);
    }

    public void reportThrottleCalibration() {
        Map<String, Long> calibrated = calibrateThrottleController.getCalibration();
        calibrationValues.put("ThrottleCalibrationLow", calibrated.get("calibrationLow"));
        calibrationValues.put("ThrottleCalibrationHigh", calibrated.get("calibrationHigh"));
        calibrationValues.put("ThrottleDeadzoneLow", calibrated.get("deadzoneLow"));
        calibrationValues.put("ThrottleDeadzoneHigh", calibrated.get("deadzoneHigh"));
    }

    public void initialize() {
        calibrateClutchController.injectMainController(this);
        calibrateThrottleController.injectMainController(this);
        calibrateBrakeController.injectMainController(this);

        saveCalibrationButton.setOnAction((event) -> {
            reportClutchCalibration();
            reportBrakeCalibration();
            reportThrottleCalibration();
            saveCalibration();
        });

        resetCalibrationButton.setOnAction((event) -> {
            resetCalibration();
        });

    }

    public void saveCalibration() {
        String TCALI = "TCALI:" + calibrationValues.get("ThrottleCalibrationLow") + "-" + calibrationValues.get("ThrottleCalibrationHigh") + "-" + calibrationValues.get("ThrottleDeadzoneLow") + "-" + calibrationValues.get("ThrottleDeadzoneHigh");
        String BCALI = "BCALI:" + calibrationValues.get("BrakeCalibrationLow") + "-" + calibrationValues.get("BrakeCalibrationHigh") + "-" + calibrationValues.get("BrakeDeadzoneLow") + "-" + calibrationValues.get("BrakeDeadzoneHigh");
        String CCALI = "CCALI:" + calibrationValues.get("ClutchCalibrationLow") + "-" + calibrationValues.get("ClutchCalibrationHigh") + "-" + calibrationValues.get("ClutchDeadzoneLow") + "-" + calibrationValues.get("ClutchDeadzoneHigh");

        String textLine = TCALI + "," + BCALI + "," + CCALI + "\n";
        this.controller.writeSerial(textLine);
    }

    private void printMap(Map<String, Integer> calibrationValues) {
        calibrationValues.forEach((key, value) -> System.out.println(key + ":" + value));
    }

    // from primary controller
    public void setBits(long throttleBitRaw, long throttleBitHid, long brakeBitRaw, long brakeBitHid, long clutchBitRaw, long clutchBitHid) {
        calibrateThrottleController.setBit(throttleBitRaw, throttleBitHid);
        calibrateBrakeController.setBit(brakeBitRaw, brakeBitHid);
        calibrateClutchController.setBit(clutchBitRaw, clutchBitHid);
    }

    public void setCalibration(long[] throttleCalibration, long[] brakeCalibration, long[] clutchCalibration) {
        calibrateClutchController.setCalibrationValues(
                clutchCalibration[0], //calibration_low
                clutchCalibration[1], //calibration_high
                clutchCalibration[2], //deadzone_low
                clutchCalibration[3]  //deadzone_high
        );
        calibrateBrakeController.setCalibrationValues(
                brakeCalibration[0], //calibration_low
                brakeCalibration[1], //calibration_high
                brakeCalibration[2], //deadzone_low
                brakeCalibration[3]  //deadzone_high
        );
        calibrateThrottleController.setCalibrationValues(
                throttleCalibration[0], //calibration_low
                throttleCalibration[1], //calibration_high
                throttleCalibration[2], //deadzone_low
                throttleCalibration[3]  //deadzone_high
        );
    }

    private void resetCalibration() {
        this.controller.writeSerial("CALIRESET\n"); //reset values on arduino
        this.controller.writeSerial("GetCali\n"); //get reset values from arduino
    }
}
