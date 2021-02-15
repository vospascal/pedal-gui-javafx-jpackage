package org.example.calibrate;

import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.example.bulletgraph.BulletGraph;

import java.util.HashMap;
import java.util.Map;

public class CalibrateClutchController {

    private CalibrateController controller;

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
    public ImageView calibrationLowImage;

    @FXML
    public ImageView calibrationHighImage;

    @FXML
    public Label calibrationInstructions;

    private Integer calibrationLow = null;
    private Boolean calibrationRunningLow = false;
    private Integer calibrationHigh = null;
    private Boolean calibrationRunningHigh = false;

    @FXML
    public Label hid_calibration_label;

    @FXML
    public Label raw_calibration_label;


    private void calibrateLow(Integer sensorValue) {
        if (calibrationLow == null) {
            calibrationLow = sensorValue;
        }
        if (sensorValue < calibrationLow) {
            calibrationLow = sensorValue;
        }
    }

    private void calibrateHigh(Integer sensorValue) {
        if (calibrationHigh == null) {
            calibrationHigh = sensorValue;
        }
        if (sensorValue > calibrationHigh) {
            calibrationHigh = sensorValue;
        }
    }


    public void injectMainController(CalibrateController calibrateController) {
        this.controller = calibrateController;
    }


    public void setValues(Map<String, Integer> pedalValues) {
//        calibration_label.setText(pedalValues.get("raw").toString());
//        rawProgressBar.setProgress(pedalValues.get("raw") / 1023d);
//        hidProgressBar.setProgress(pedalValues.get("hid") / 1023d);

        rawProgressChart.setUpperBound(1023d);
        rawProgressChart.setPerformanceMeasure(pedalValues.get("raw"));
        raw_calibration_label.setText(pedalValues.get("raw").toString());

        hidProgressChart.setUpperBound(1023d);
        hidProgressChart.setPerformanceMeasure(pedalValues.get("hid"));
        hid_calibration_label.setText(pedalValues.get("hid").toString());

        if (calibrationRunningLow) {
            calibrateLow(pedalValues.get("raw"));
        }
        if (calibrationRunningHigh) {
            calibrateHigh(pedalValues.get("raw"));
        }
    }

    public void calibrationValues(int[] values) {
        System.out.println(values[0] + " low");
        System.out.println(values[0] + " high");
    }

    private Map<String, Integer> calibrationMap() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("high", calibrationHigh);
        map.put("low", calibrationLow);
        return map;
    }

    public void initialize() {
        calibrationHighButton.setOnAction((event) -> {
            calibrationHigh = null;
            calibrationLow = null;

            calibrationRunningHigh = true;
            calibrationHighImage.setVisible(true);

            calibrationHighButton.setVisible(false);
            calibrationLowButton.setVisible(true);
            calibrationDoneButton.setVisible(false);

            calibrationInstructions.setText("Press the clutch al the way down and press done");
        });

        calibrationLowButton.setOnAction((event) -> {
            calibrationRunningHigh = false;
            calibrationHighImage.setVisible(false);

            calibrationRunningLow = true;
            calibrationLowImage.setVisible(true);

            calibrationHighButton.setVisible(false);
            calibrationLowButton.setVisible(false);
            calibrationDoneButton.setVisible(true);

            calibrationInstructions.setText("release the clutch to the neutral position and press done");
        });


        calibrationDoneButton.setOnAction((event) -> {
            calibrationRunningLow = false;
            calibrationLowImage.setVisible(false);

            calibrationHighButton.setVisible(true);
            calibrationLowButton.setVisible(false);
            calibrationDoneButton.setVisible(false);
            calibrationInstructions.setText("");
            controller.reportClutchCalibration(calibrationMap());
        });


        rawProgressChart.setTitle("");
        rawProgressChart.setDescription("");
        rawProgressChart.setOrientation(Orientation.VERTICAL);
        rawProgressChart.setHigherDeadzone(900);
        rawProgressChart.setLowerDeadzone(200);
        rawProgressChart.setLowerCalibration(75);
        rawProgressChart.setHigherCalibration(475);
//        rawProgressChart.setComparativeMeasure(600);

        hidProgressChart.setTitle("");
        hidProgressChart.setDescription("");
        hidProgressChart.setOrientation(Orientation.VERTICAL);

    }
}
