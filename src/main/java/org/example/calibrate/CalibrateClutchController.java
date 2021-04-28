package org.example.calibrate;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.bulletgraph.BulletGraph;

import java.util.HashMap;
import java.util.Map;

public class CalibrateClutchController {
    private CalibrateController controller;
    Map<String, Long> calibrationMapValues = new HashMap<String, Long>();
    private Boolean calibrationRunningLow = false;
    private Boolean calibrationRunningHigh = false;
    private int sensorBit;

    @FXML
    public BulletGraph hidProgressChart;

    @FXML
    public BulletGraph rawProgressChart;

    @FXML
    public Button calibrationHighButton;

    @FXML
    public Button calibrationLowButton;

    @FXML
    public Button calibrationDoneButton;

    @FXML
    public Label calibrationInstructions;

    @FXML
    public Label hid_calibration_label;

    @FXML
    public Label raw_calibration_label;

    @FXML
    public TextField deadzoneHighField;

    @FXML
    public TextField deadzoneLowField;

    public void injectMainController(CalibrateController calibrateController) {
        this.controller = calibrateController;
    }

    private void calibrateLow(Long sensorValue) {
        if (calibrationMapValues.get("calibrationLow") == null) {
            calibrationMapValues.put("calibrationLow", sensorValue);
            rawProgressChart.setLowerCalibration(sensorValue);
        }
        if (sensorValue < calibrationMapValues.get("calibrationLow")) {
            calibrationMapValues.put("calibrationLow", sensorValue);
            rawProgressChart.setLowerCalibration(sensorValue);
        }
    }

    private void calibrateHigh(Long sensorValue) {
        if (calibrationMapValues.get("calibrationHigh") == null) {
            calibrationMapValues.put("calibrationHigh", sensorValue);
            rawProgressChart.setHigherCalibration(sensorValue);
        }
        if (sensorValue > calibrationMapValues.get("calibrationHigh")) {
            calibrationMapValues.put("calibrationHigh", sensorValue);
            rawProgressChart.setHigherCalibration(sensorValue);
        }
    }

    public void setBit(int bit) {
        sensorBit = bit;
    }

    public Map<String, Long> getCalibration(){
        return calibrationMapValues;
    }

    public void setValues(Map<String, Long> pedalValues) {
        rawProgressChart.setUpperBound(sensorBit);
        rawProgressChart.setPerformanceMeasure(pedalValues.get("raw"));
        raw_calibration_label.setText(pedalValues.get("raw").toString());

        hidProgressChart.setUpperBound(sensorBit);
        hidProgressChart.setPerformanceMeasure(pedalValues.get("hid"));
        hid_calibration_label.setText(pedalValues.get("hid").toString());

        if (calibrationRunningLow) {
            calibrateLow(pedalValues.get("raw"));
        }
        if (calibrationRunningHigh) {
            calibrateHigh(pedalValues.get("raw"));
        }
    }

    public void setCalibrationValues(long calibration_low, long calibration_high, long deadzone_low, long deadzone_high) {
        calibrationMapValues.put("calibrationLow", calibration_low);
        rawProgressChart.setLowerCalibration(calibration_low);

        rawProgressChart.setHigherCalibration(calibration_high);
        calibrationMapValues.put("calibrationHigh", calibration_high);

        rawProgressChart.setLowerDeadzone(deadzone_low);
        deadzoneLowField.setText(Long.toString(deadzone_low));
        calibrationMapValues.put("deadzoneLow", deadzone_low);

        rawProgressChart.setHigherDeadzone(deadzone_high);
        deadzoneHighField.setText(Long.toString(deadzone_high));
        calibrationMapValues.put("deadzoneHigh", deadzone_high);
    }

    private void printMap(Map<String, Integer> calibrationValues){
        calibrationValues.forEach((key, value) -> System.out.println(key + ":" + value));
    }

    public void initialize() {
        deadzoneLowField.textProperty().addListener((observable, oldValue, newValue) -> {
            calibrationMapValues.put("deadzoneLow", Long.parseLong(newValue));
            rawProgressChart.setLowerDeadzone(Integer.parseInt(newValue));
        });
        deadzoneHighField.textProperty().addListener((observable, oldValue, newValue) -> {
            calibrationMapValues.put("deadzoneHigh", Long.parseLong(newValue));
            rawProgressChart.setHigherDeadzone(Integer.parseInt(newValue));
        });

        calibrationHighButton.setOnAction((event) -> {
            calibrationMapValues.put("calibrationHigh", null);
            calibrationMapValues.put("calibrationLow", null);
            calibrationRunningHigh = true;
            calibrationHighButton.setVisible(false);
            calibrationLowButton.setVisible(true);
            calibrationDoneButton.setVisible(false);
            calibrationInstructions.setText("Press the clutch al the way down and press next");
        });

        calibrationLowButton.setOnAction((event) -> {
            calibrationRunningHigh = false;
            calibrationRunningLow = true;
            calibrationHighButton.setVisible(false);
            calibrationLowButton.setVisible(false);
            calibrationDoneButton.setVisible(true);
            calibrationInstructions.setText("release the clutch to the neutral position and press done");
        });

        calibrationDoneButton.setOnAction((event) -> {
            calibrationRunningLow = false;
            calibrationHighButton.setVisible(true);
            calibrationLowButton.setVisible(false);
            calibrationDoneButton.setVisible(false);
            calibrationInstructions.setText("");
            rawProgressChart.setLowerCalibration(calibrationMapValues.get("calibrationLow"));
            rawProgressChart.setHigherCalibration(calibrationMapValues.get("calibrationHigh"));
        });
    }
}
