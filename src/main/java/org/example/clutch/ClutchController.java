package org.example.clutch;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import org.example.PrimaryController;

import java.util.Map;

public class ClutchController {
    private PrimaryController controller;

    @FXML
    private LineChart clutchChart;

    @FXML
    private ProgressBar clutchProgressBar;

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

    XYChart.Series series1 = new XYChart.Series();
    XYChart.Series series2 = new XYChart.Series();
    XYChart.Series series3 = new XYChart.Series();

    public void injectMainController(PrimaryController primaryController){
        this.controller = primaryController;
    }


    public void setClutchPosition(Map<String, Integer> clutchValues) {
        series3.getData().clear();
        series3.getData().add(new XYChart.Data(clutchValues.get("after"), clutchValues.get("before")));

        // casting int to double
        clutchProgressBar.setProgress(clutchValues.get("after")/100d);
    }

    public void setClutchMap(int[] clutchMap) {
        int[] mapData = clutchMap;
        int[] baseData = new int[]{0, 20, 40, 60, 80, 100};
        series2.getData().clear();

        for(int i=0; i < baseData.length; i++){
            series2.getData().add(new XYChart.Data(baseData[i], mapData[i]));
        }

        input_0.setText(String.valueOf(mapData[0]));
        input_20.setText(String.valueOf(mapData[1]));
        input_40.setText(String.valueOf(mapData[2]));
        input_60.setText(String.valueOf(mapData[3]));
        input_80.setText(String.valueOf(mapData[4]));
        input_100.setText(String.valueOf(mapData[5]));
    }

    public void handleAction(ActionEvent actionEvent) {
        String textLine = "CMAP:" + input_0.getText() + "-" + input_20.getText() + "-"+ input_40.getText() + "-"+ input_60.getText() + "-"+ input_80.getText() + "-"+ input_100.getText();
        this.controller.writeSerial(textLine);
    }

    public void initialize() {
        input_0.textProperty().addListener((observable, oldValue, newValue) -> {
            series2.getData().set(0, new XYChart.Data(0,  Integer.parseInt(newValue)));
        });

        input_20.textProperty().addListener((observable, oldValue, newValue) -> {
            series2.getData().set(1, new XYChart.Data(20,  Integer.parseInt(newValue)));
        });

        input_40.textProperty().addListener((observable, oldValue, newValue) -> {
            series2.getData().set(2, new XYChart.Data(40,  Integer.parseInt(newValue)));
        });

        input_60.textProperty().addListener((observable, oldValue, newValue) -> {
            series2.getData().set(3, new XYChart.Data(60,  Integer.parseInt(newValue)));
        });

        input_80.textProperty().addListener((observable, oldValue, newValue) -> {
            series2.getData().set(4, new XYChart.Data(80,  Integer.parseInt(newValue)));
        });

        input_100.textProperty().addListener((observable, oldValue, newValue) -> {
            series2.getData().set(5, new XYChart.Data(100,  Integer.parseInt(newValue)));
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

        clutchChart.getData().addAll(series1, series2, series3);
        clutchChart.setAnimated(false); // disable animations
        clutchChart.setLegendVisible(false);

    }

}
