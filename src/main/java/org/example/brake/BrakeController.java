package org.example.brake;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import org.example.PrimaryController;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;

public class BrakeController {
    private PrimaryController controller;

    @FXML
    private LineChart brakeChart;

    @FXML
    private ProgressBar brakeProgressBar;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private TextField input_0;

    @FXML
    private TextField input_20;

    @FXML
    private TextField input_40;

    @FXML
    private TextField input_60;

    @FXML
    private TextField input_80;

    @FXML
    private TextField input_100;

    @FXML
    private CheckBox inverted;

    int[] sCurveFastSlowMap = {0, 60, 75, 80, 85, 100};
    //s curve slow fast [0 31 46 54 69 100]
    //s curve slow fast [0 19 31 40 46 50 54 60 69 81 100]
    int[] sCurveSlowFast = {0, 31, 46, 54, 69, 100};
    //very fast curve     [0 52 75 89 96 100]
    //very fast curve     [0 33 52 65 75 83 89 93 96 98 100]
    int[] veryFastCurveMap = {0, 52, 75, 89, 96, 100};
    //fast curve          [0 37 61 79 91 100]
    //fast curve          [0 21 37 50 61 71 79 86 91 96 100]
    int[] fastCurveMap = {0, 37, 61, 79, 91, 100};
    //very slow curve     [0 4 11 25 48 100]
    //very slow curve     [0 2 4 7 11 19 25 35 48 66 100]
    int[] verySlowCurveMap = {0, 4, 11, 25, 48, 100};
    //slow curve          [0 9 21 39 63 100]
    //slow curve          [0 4 9 14 21 29 39 50 63 79 100]
    int[] slowCurveMap = {0, 9, 21, 39, 63, 100};
    //linear              [0 20 40 60 80 100]
    //linear              [0 10 20 30 40 50 60 70 80 90 100]
    int[] linearMap = {0, 20, 40, 60, 80, 100};

    ObservableList<String> curvesList = FXCollections.observableArrayList(
            "", "linear", "slow curve", "very slow curve", "fast curve", "very fast curve", "s curve fast slow", "s curve slow fast"
    );

    @FXML
    private ChoiceBox curves;

    XYChart.Series series1 = new XYChart.Series();
    XYChart.Series series2 = new XYChart.Series();
    XYChart.Series series3 = new XYChart.Series();

    public void injectMainController(PrimaryController primaryController) {
        this.controller = primaryController;
    }

    public void setBrakePosition(Map<String, Integer> brakeValues) {
        series3.getData().clear();
        series3.getData().add(new XYChart.Data(brakeValues.get("after"), brakeValues.get("before")));

        brakeProgressBar.setProgress(brakeValues.get("after") / 100d);
    }

    public void setInverted(String invertedValue) {
        System.out.println((invertedValue.equals("1") ? true : false) + " brake");
        inverted.setSelected(invertedValue.equals("1") ? true : false);
    }

    private String getMapType(int[] mapData) {
        if (Arrays.equals(mapData, sCurveFastSlowMap)) {
            return "s curve fast slow";
        }
        if (Arrays.equals(mapData, sCurveSlowFast)) {
            return "s curve slow fast";
        }
        if (Arrays.equals(mapData, veryFastCurveMap)) {
            return "very fast curve";
        }
        if (Arrays.equals(mapData, fastCurveMap)) {
            return "fast curve";
        }
        if (Arrays.equals(mapData, verySlowCurveMap)) {
            return "very slow curve";
        }
        if (Arrays.equals(mapData, slowCurveMap)) {
            return "slow curve";
        }
        if (Arrays.equals(mapData, linearMap)) {
            return "linear";
        }

        return "";

    }

    public void setBrakeMap(int[] brakeMap) {
        int[] mapData = brakeMap;
        int[] baseData = new int[]{0, 20, 40, 60, 80, 100};
        series2.getData().clear();

        for (int i = 0; i < baseData.length; i++) {
            series2.getData().add(new XYChart.Data(baseData[i], mapData[i]));
        }

        input_0.setText(String.valueOf(mapData[0]));
        input_20.setText(String.valueOf(mapData[1]));
        input_40.setText(String.valueOf(mapData[2]));
        input_60.setText(String.valueOf(mapData[3]));
        input_80.setText(String.valueOf(mapData[4]));
        input_100.setText(String.valueOf(mapData[5]));

        curves.setValue(getMapType(mapData));
    }

    public String saveBMAPSettings() {
        String textLine = input_0.getText() + "-" + input_20.getText() + "-" + input_40.getText() + "-" + input_60.getText() + "-" + input_80.getText() + "-" + input_100.getText();
        return textLine;
    }

    public String saveInvertedSettings() {
        String invertedString = Boolean.toString(inverted.isSelected());
        return invertedString.toLowerCase().equals("true") ? "1" : "0";
    }

    private void checkIfMatchCurveList(String newValue, int index, XYChart.Series series2) {
        int[] map = {0, 0, 0, 0, 0, 0};
        for (int i = 0; i < map.length; i++) {
            XYChart.Data<Number, Number> temp = (XYChart.Data<Number, Number>) series2.getData().get(i);
            Array.set(map, i, temp.getYValue());
        }
        Array.set(map, index, Integer.parseInt(newValue));
//        System.out.println(Arrays.toString(map));
//        System.out.println(getMapType(map));
        curves.setValue(getMapType(map));
    }

    public void initialize() {
        input_0.textProperty().addListener((observable, oldValue, newValue) -> {
            series2.getData().set(0, new XYChart.Data(0, Integer.parseInt(newValue)));
            checkIfMatchCurveList(newValue, 0, series2);
        });

        input_20.textProperty().addListener((observable, oldValue, newValue) -> {
            series2.getData().set(1, new XYChart.Data(20, Integer.parseInt(newValue)));
            checkIfMatchCurveList(newValue, 1, series2);
        });

        input_40.textProperty().addListener((observable, oldValue, newValue) -> {
            series2.getData().set(2, new XYChart.Data(40, Integer.parseInt(newValue)));
            checkIfMatchCurveList(newValue, 2, series2);
        });

        input_60.textProperty().addListener((observable, oldValue, newValue) -> {
            series2.getData().set(3, new XYChart.Data(60, Integer.parseInt(newValue)));
            checkIfMatchCurveList(newValue, 3, series2);
        });

        input_80.textProperty().addListener((observable, oldValue, newValue) -> {
            series2.getData().set(4, new XYChart.Data(80, Integer.parseInt(newValue)));
            checkIfMatchCurveList(newValue, 4, series2);
        });

        input_100.textProperty().addListener((observable, oldValue, newValue) -> {
            series2.getData().set(5, new XYChart.Data(100, Integer.parseInt(newValue)));
            checkIfMatchCurveList(newValue, 5, series2);
        });

        xAxis.setForceZeroInRange(false);
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(100);
        xAxis.setTickUnit(20);

        yAxis.setForceZeroInRange(false);
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(100);
        yAxis.setTickUnit(20);

        series1.getData().add(new XYChart.Data(0, 0));
        series1.getData().add(new XYChart.Data(20, 20));
        series1.getData().add(new XYChart.Data(40, 40));
        series1.getData().add(new XYChart.Data(60, 60));
        series1.getData().add(new XYChart.Data(80, 80));
        series1.getData().add(new XYChart.Data(100, 100));

        series3.getData().add(new XYChart.Data(50, 50));

        brakeChart.getData().addAll(series1, series2, series3);
        brakeChart.setAnimated(false); // disable animations
        brakeChart.setLegendVisible(false);

        curves.setItems(curvesList);
        curves.setValue("");

        curves.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("linear")) {
                setBrakeMap(linearMap);
            }
            if (newValue.equals("slow curve")) {
                setBrakeMap(slowCurveMap);
            }
            if (newValue.equals("very slow curve")) {
                setBrakeMap(verySlowCurveMap);
            }
            if (newValue.equals("fast curve")) {
                setBrakeMap(fastCurveMap);
            }
            if (newValue.equals("very fast curve")) {
                setBrakeMap(veryFastCurveMap);
            }
            if (newValue.equals("s curve fast slow")) {
                setBrakeMap(sCurveFastSlowMap);
            }
            if (newValue.equals("s curve slow fast")) {
                setBrakeMap(sCurveSlowFast);
            }

        });
    }
}
