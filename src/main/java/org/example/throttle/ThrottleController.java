package org.example.throttle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.example.PrimaryController;
import org.example.UserStorageAndConfiguration;

import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ThrottleController {
    private PrimaryController primaryController;

    @FXML
    private LineChart throttleChart;

    @FXML
    private ProgressBar throttleProgressBar;

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

    @FXML
    public Label ThrottleLabel;

    @FXML
    private CheckBox smooth;

    int[] sCurveFastSlowMap = {0, 60, 75, 80, 85, 100};
    //s curve slow fast [0 31 46 54 69 100]
    //s curve slow fast [0 19 31 40 46 50 54 60 69 81 100]
    int[] sCurveSlowFastMap = {0, 31, 46, 54, 69, 100};
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


    Label emptyLabel = new Label("");
    Label linearLabel = new Label("linear");
    Label slowCurveLabel = new Label("slow curve");
    Label verySlowCurveLabel = new Label("very slow curve");
    Label fastCurveLabel = new Label("fast curve");
    Label veryFastCurveLabel = new Label("very fast curve");
    Label sCurveFastSlowLabel = new Label("s curve fast slow");
    Label sCurveSlowFastLabel = new Label("s curve slow fast");


    ObservableList<Label> curvesList = FXCollections.observableArrayList(
        emptyLabel,
        linearLabel,
        slowCurveLabel,
        verySlowCurveLabel,
        fastCurveLabel,
        veryFastCurveLabel,
        sCurveFastSlowLabel,
        sCurveSlowFastLabel
    );

    @FXML
    private ComboBox<Label> curves;

    XYChart.Series series1 = new XYChart.Series();
    XYChart.Series series2 = new XYChart.Series();
    XYChart.Series series3 = new XYChart.Series();

    public void injectMainController(PrimaryController primaryController) {
        this.primaryController = primaryController;
    }

    public void setThrottlePosition(Map<String, Long> throttleValues) {
        series3.getData().clear();
        series3.getData().add(new XYChart.Data(throttleValues.get("after"), throttleValues.get("before")));

        throttleProgressBar.setProgress(throttleValues.get("after") / 100d);
    }

    public void setInverted(String invertedValue) {
        inverted.setSelected(invertedValue.equals("1") ? true : false);
    }

    public void setSmooth(String smoothValue) {
        smooth.setSelected(smoothValue.equals("1") ? true : false);
    }

    private String getMapType(int[] mapData) {
        if (Arrays.equals(mapData, sCurveFastSlowMap)) {
            return sCurveFastSlowLabel.getText();
        } else if (Arrays.equals(mapData, sCurveSlowFastMap)) {
            return sCurveSlowFastLabel.getText();
        } else if (Arrays.equals(mapData, veryFastCurveMap)) {
            return veryFastCurveLabel.getText();
        } else if (Arrays.equals(mapData, fastCurveMap)) {
            return fastCurveLabel.getText();
        } else if (Arrays.equals(mapData, verySlowCurveMap)) {
            return verySlowCurveLabel.getText();
        } else if (Arrays.equals(mapData, slowCurveMap)) {
            return slowCurveLabel.getText();
        } else if (Arrays.equals(mapData, linearMap)) {
            return linearLabel.getText();
        }

        return emptyLabel.getText();

    }

    public void setThrottleMap(int[] throttleMap) {
        int[] mapData = throttleMap;
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

        if(getMapType(mapData) != ""){
            int activeIndex = (int) getActiveTextAndIndex(getMapType(mapData)).get("index");
            curves.getSelectionModel().select(activeIndex);
        } else {
            curves.getSelectionModel().select(0);
        }
    }

    public String saveTMAPSettings() {
        String textLine = input_0.getText() + "-" + input_20.getText() + "-" + input_40.getText() + "-" + input_60.getText() + "-" + input_80.getText() + "-" + input_100.getText();
        return textLine;
    }

    public String saveInvertedSettings() {
        String invertedString = Boolean.toString(inverted.isSelected());
        return invertedString.toLowerCase().equals("true") ? "1" : "0";
    }

    public String saveSmoothSettings() {
        String smoothString = Boolean.toString(smooth.isSelected());
        return smoothString.toLowerCase().equals("true") ? "1" : "0";
    }

    private void checkIfMatchCurveList(String newValue, int index, XYChart.Series series2) {
        int[] map = {0, 0, 0, 0, 0, 0};
        for (int i = 0; i < map.length; i++) {
            XYChart.Data<Number, Number> temp = (XYChart.Data<Number, Number>) series2.getData().get(i);
            Array.set(map, i, temp.getYValue());
        }
        Array.set(map, index, Integer.parseInt(newValue));

        if(getMapType(map) != ""){
            int activeIndex = (int) getActiveTextAndIndex(getMapType(map)).get("index");
            curves.getSelectionModel().select(activeIndex);
        } else {
            curves.getSelectionModel().select(0);
        }


    }

    public void initialize() {
        ThrottleLabel.textProperty().bind(UserStorageAndConfiguration.createStringBinding("throttle"));

        UserStorageAndConfiguration.bindLocaleKey(emptyLabel,"tab.pedals.curves.empty");
        UserStorageAndConfiguration.bindLocaleKey(linearLabel,"tab.pedals.curves.linear");
        UserStorageAndConfiguration.bindLocaleKey(slowCurveLabel,"tab.pedals.curves.slow.curve");
        UserStorageAndConfiguration.bindLocaleKey(verySlowCurveLabel,"tab.pedals.curves.very.slow.curve");
        UserStorageAndConfiguration.bindLocaleKey(fastCurveLabel,"tab.pedals.curves.fast.curve");
        UserStorageAndConfiguration.bindLocaleKey(veryFastCurveLabel, "tab.pedals.curves.very.fast.curve");
        UserStorageAndConfiguration.bindLocaleKey(sCurveFastSlowLabel,"tab.pedals.curves.scurve.fast.slow");
        UserStorageAndConfiguration.bindLocaleKey(sCurveSlowFastLabel,"tab.pedals.curves.scurve.slow.fast");

        UserStorageAndConfiguration.bindLocaleKey(smooth,"tab.pedals.label.smooth");
        UserStorageAndConfiguration.bindLocaleKey(inverted,"tab.pedals.label.inverted");

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

        throttleChart.getData().addAll(series1, series2, series3);
        throttleChart.setAnimated(false); // disable animations
        throttleChart.setLegendVisible(false);

        curves.setItems(curvesList);
        curves.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Label> call(ListView<Label> param) {
                return new ListCell<Label>() {
                    @Override
                    protected void updateItem(Label item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getText());
                        }
                    }
                };
            }
        });

        // hack to update list of items
        curves.addEventHandler(ComboBox.ON_SHOWING, event -> {
            // remove a item so list gets updated..
            curves.getItems().remove(0);
            // add same item so list is completed again and updated..
            curves.getItems().add(0, emptyLabel);
        });

        curves.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                int activeIndex = (int) getActiveTextAndIndex(newValue.toString()).get("index");
                int[] activeMap = (int[]) getActiveTextAndIndex(newValue.toString()).get("map");
                curves.getSelectionModel().select(activeIndex);
                if(activeMap != null){
                    setThrottleMap(activeMap);
                }
            }
        });
    }

    public Map<String, Object> getActiveTextAndIndex(String newValue) {
        Map<String, Object> map = new HashMap<>();

        if (newValue.contains(MessageFormat.format("''{0}''", linearLabel.getText())) || newValue.equalsIgnoreCase(linearLabel.getText())) {
            map.put("text", linearLabel.getText());
            map.put("label", linearLabel);
            map.put("index", 1);
            map.put("map", linearMap);
        }else if (newValue.contains(MessageFormat.format("''{0}''", slowCurveLabel.getText())) || newValue.equalsIgnoreCase(slowCurveLabel.getText())) {
            map.put("text", slowCurveLabel.getText());
            map.put("label", slowCurveLabel);
            map.put("index", 2);
            map.put("map", slowCurveMap);
        } else if  (newValue.contains(MessageFormat.format("''{0}''", verySlowCurveLabel.getText())) || newValue.equalsIgnoreCase(verySlowCurveLabel.getText())) {
            map.put("text", verySlowCurveLabel.getText());
            map.put("label", verySlowCurveLabel);
            map.put("index", 3);
            map.put("map", verySlowCurveMap);
        } else if (newValue.contains(MessageFormat.format("''{0}''", fastCurveLabel.getText())) || newValue.equalsIgnoreCase(fastCurveLabel.getText())) {
            map.put("text", fastCurveLabel.getText());
            map.put("label", fastCurveLabel);
            map.put("index", 4);
            map.put("map", fastCurveMap);
        } else if (newValue.contains(MessageFormat.format("''{0}''", veryFastCurveLabel.getText())) || newValue.equalsIgnoreCase(veryFastCurveLabel.getText())) {
            map.put("text", veryFastCurveLabel.getText());
            map.put("label", veryFastCurveLabel);
            map.put("index", 5);
            map.put("map", veryFastCurveMap);
        } else if (newValue.contains(MessageFormat.format("''{0}''", sCurveFastSlowLabel.getText())) || newValue.equalsIgnoreCase(sCurveFastSlowLabel.getText())) {
            map.put("text", sCurveFastSlowLabel.getText());
            map.put("label", sCurveFastSlowLabel);
            map.put("index", 6);
            map.put("map", sCurveFastSlowMap);
        } else if (newValue.contains(MessageFormat.format("''{0}''", sCurveSlowFastLabel.getText())) || newValue.equalsIgnoreCase(sCurveSlowFastLabel.getText())) {
            map.put("text", sCurveSlowFastLabel.getText());
            map.put("label", sCurveSlowFastLabel);
            map.put("index", 7);
            map.put("map", sCurveSlowFastMap);
        } else if (newValue.contains(MessageFormat.format("''{0}''", emptyLabel.getText())) || newValue.equalsIgnoreCase(emptyLabel.getText())) {
            map.put("text", emptyLabel.getText());
            map.put("label", emptyLabel);
            map.put("index", 0);
            map.put("map", null);
        }

        return map;
    }
}

