package org.example.calibrate;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.controlsfx.control.RangeSlider;
import org.example.UserStorageAndConfiguration;
import org.example.bulletgraph.BulletGraph;

import java.util.HashMap;
import java.util.Map;

public class CalibrateBrakeController {
    private CalibrateController controller;
    Map<String, Long> calibrationMapValues = new HashMap<String, Long>();
    private Boolean calibrationRunningLow = false;
    private Boolean calibrationRunningHigh = false;
    private long rawBit;
    private long hidBit;

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
    public Label hid_calibration_label_value;
    @FXML
    public Label hid_calibration_value;

    @FXML
    public Label raw_calibration_label;
    @FXML
    public Label raw_calibration_label_value;
    @FXML
    public Label raw_calibration_value;

    @FXML
    private RangeSlider rangeSlider;

    @FXML
    public Label topCalibration_label;

    @FXML
    public Label topCalibration_value;

    @FXML
    public Label bottomCalibration_label;

    @FXML
    public Label bottomCalibration_value;


    @FXML
    public Label topDeadzone_label;
    @FXML
    public Label topDeadzone_value;

    @FXML
    public Label bottomDeadzone_label;
    @FXML
    public Label bottomDeadzone_value;

    @FXML
    public Label BrakeLabel;


    public void injectMainController(CalibrateController calibrateController) {
        this.controller = calibrateController;
    }

    private void calibrateLow(Long sensorValue) {
        if (calibrationMapValues.get("calibrationLow") == null) {
            calibrationMapValues.put("calibrationLow", sensorValue);
            rawProgressChart.setLowerCalibration(sensorValue);
            bottomCalibration_value.setText(String.valueOf(sensorValue));
        }
        if (sensorValue < calibrationMapValues.get("calibrationLow")) {
            calibrationMapValues.put("calibrationLow", sensorValue);
            rawProgressChart.setLowerCalibration(sensorValue);
            bottomCalibration_value.setText(String.valueOf(sensorValue));
        }
    }

    private void calibrateHigh(Long sensorValue) {
        if (calibrationMapValues.get("calibrationHigh") == null) {
            calibrationMapValues.put("calibrationHigh", sensorValue);
            rawProgressChart.setHigherCalibration(sensorValue);
            topCalibration_value.setText(String.valueOf(sensorValue));
        }
        if (sensorValue > calibrationMapValues.get("calibrationHigh")) {
            calibrationMapValues.put("calibrationHigh", sensorValue);
            rawProgressChart.setHigherCalibration(sensorValue);
            topCalibration_value.setText(String.valueOf(sensorValue));
        }
    }

    public void setBit(long bitRaw, long bitHid) {
        rawBit = bitRaw;
        hidBit = bitHid;
    }

    public Map<String, Long> getCalibration(){
        return calibrationMapValues;
    }

    public void setValues(Map<String, Long> pedalValues) {
        rawProgressChart.setUpperBound(rawBit);
        rawProgressChart.setPerformanceMeasure(pedalValues.get("raw"));
        raw_calibration_value.setText(pedalValues.get("raw").toString());
        raw_calibration_label_value.setText(pedalValues.get("raw").toString());

        hidProgressChart.setUpperBound(hidBit);
        hidProgressChart.setPerformanceMeasure(pedalValues.get("hid"));
        hid_calibration_value.setText(pedalValues.get("hid").toString());
        hid_calibration_label_value.setText(pedalValues.get("hid").toString());
        if (calibrationRunningLow) {
            calibrateLow(pedalValues.get("raw"));
        }
        if (calibrationRunningHigh) {
            calibrateHigh(pedalValues.get("raw"));
        }
    }

    public void setCalibrationValues(long calibration_low, long calibration_high, long deadzone_low, long deadzone_high) {
        double getOneProcent = (double) rawBit / 100;

        calibrationMapValues.put("calibrationLow", calibration_low);
        rawProgressChart.setLowerCalibration(calibration_low);
        bottomCalibration_value.setText(String.valueOf(calibration_low));

        rawProgressChart.setHigherCalibration(calibration_high);
        calibrationMapValues.put("calibrationHigh", calibration_high);
        topCalibration_value.setText(String.valueOf(calibration_high));

        rawProgressChart.setLowerDeadzone(deadzone_low);
        double testLow = Math.round((double) deadzone_low / getOneProcent);
        rangeSlider.setLowValue(Math.round((long)testLow));
        calibrationMapValues.put("deadzoneLow", deadzone_low);
        bottomDeadzone_value.setText(String.valueOf(deadzone_low));

        rawProgressChart.setHigherDeadzone(deadzone_high);
        double testHigh = Math.round((double) deadzone_high / getOneProcent);
        rangeSlider.setHighValue((long)testHigh);
        calibrationMapValues.put("deadzoneHigh", deadzone_high);
        topDeadzone_value.setText(String.valueOf(deadzone_high));
    }

    private void printMap(Map<String, Integer> calibrationValues){
        calibrationValues.forEach((key, value) -> System.out.println(key + ":" + value));
    }

    public void initialize() {
        BrakeLabel.textProperty().bind(UserStorageAndConfiguration.createStringBinding("brake"));

        topCalibration_label.textProperty().bind(UserStorageAndConfiguration.createStringBinding("tab.calibration.label.calibrated.top"));
        bottomCalibration_label.textProperty().bind(UserStorageAndConfiguration.createStringBinding("tab.calibration.label.calibrated.bottom"));
        topDeadzone_label.textProperty().bind(UserStorageAndConfiguration.createStringBinding("tab.calibration.label.deadzone.top"));
        bottomDeadzone_label.textProperty().bind(UserStorageAndConfiguration.createStringBinding("tab.calibration.label.deadzone.bottom"));
        hid_calibration_label.textProperty().bind(UserStorageAndConfiguration.createStringBinding("tab.calibration.label.hid"));
        raw_calibration_label.textProperty().bind(UserStorageAndConfiguration.createStringBinding("tab.calibration.label.raw"));

        calibrationHighButton.textProperty().bind(UserStorageAndConfiguration.createStringBinding("tab.calibration.button.start"));
        calibrationLowButton.textProperty().bind(UserStorageAndConfiguration.createStringBinding("tab.calibration.button.next"));
        calibrationDoneButton.textProperty().bind(UserStorageAndConfiguration.createStringBinding("tab.calibration.button.done"));

        rangeSlider.setBlockIncrement(1);
        rangeSlider.setMajorTickUnit(10);
        rangeSlider.setMinorTickCount(5);
        rangeSlider.setSnapToTicks(true);
        rangeSlider.setShowTickMarks(true);
        rangeSlider.setShowTickLabels(true);
        rangeSlider.lowValueProperty().addListener((value, oldValue, newValue) -> {
            double getOneProcent = (double) rawBit / 100;
            long result = Math.round(getOneProcent * (double) newValue);
            calibrationMapValues.put("deadzoneLow", result);
            rawProgressChart.setLowerDeadzone(result);
            bottomDeadzone_value.setText(String.valueOf(result));
        });
        rangeSlider.highValueProperty().addListener((value, oldValue, newValue) -> {
            double getOneProcent = (double) rawBit / 100;
            long result = Math.round(getOneProcent * (double) newValue);
            calibrationMapValues.put("deadzoneHigh", result);
            rawProgressChart.setHigherDeadzone(result);
            topDeadzone_value.setText(String.valueOf(result));
        });

        calibrationHighButton.setOnAction((event) -> {
            calibrationMapValues.put("calibrationHigh", null);
            calibrationMapValues.put("calibrationLow", null);
            calibrationRunningHigh = true;
            calibrationHighButton.setVisible(false);
            calibrationLowButton.setVisible(true);
            calibrationDoneButton.setVisible(false);

            raw_calibration_value.setVisible(false);
            hid_calibration_value.setVisible(false);

            calibrationInstructions.textProperty().bind(UserStorageAndConfiguration.createStringBinding("tab.calibration.label.next"));
//            calibrationInstructions.setText("Press the brake al the way down and press next");
        });

        calibrationLowButton.setOnAction((event) -> {
            calibrationRunningHigh = false;
            calibrationRunningLow = true;
            calibrationHighButton.setVisible(false);
            calibrationLowButton.setVisible(false);
            calibrationDoneButton.setVisible(true);

            raw_calibration_value.setVisible(false);
            hid_calibration_value.setVisible(false);

            calibrationInstructions.textProperty().bind(UserStorageAndConfiguration.createStringBinding("tab.calibration.label.done"));
//            calibrationInstructions.setText("release the brake to the neutral position and press done");
        });

        calibrationDoneButton.setOnAction((event) -> {
            calibrationRunningLow = false;
            calibrationHighButton.setVisible(true);
            calibrationLowButton.setVisible(false);
            calibrationDoneButton.setVisible(false);
            calibrationInstructions.textProperty().bind(UserStorageAndConfiguration.createStringBinding(""));

            raw_calibration_value.setVisible(true);
            hid_calibration_value.setVisible(true);

            rawProgressChart.setLowerCalibration(calibrationMapValues.get("calibrationLow"));
            rawProgressChart.setHigherCalibration(calibrationMapValues.get("calibrationHigh"));
        });


    }
}
