package org.example.time;

import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.example.PrimaryController;

import java.text.SimpleDateFormat;
import java.util.Map;

import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;

public class TimeController {
    @FXML
    private LineChart timeChart;

    @FXML
    private CategoryAxis timexAxis;

    @FXML
    private NumberAxis timeyAxis;

    final int WINDOW_SIZE = 10;
    private ScheduledExecutorService scheduledExecutorService;
    // this is used to display time in HH:mm:ss format
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss:SS");

    XYChart.Series series1 = new XYChart.Series();
    XYChart.Series series2 = new XYChart.Series();
    XYChart.Series series3 = new XYChart.Series();

    public void injectMainController(PrimaryController primaryController){
//        this.controller = primaryController;
    }

    public void setThrottlePosition(Map<String, Integer> throttleValues) {
        Date now = new Date();
        series1.getData().add(new XYChart.Data( simpleDateFormat.format(now), throttleValues.get("after")));
        if (series1.getData().size() > 250){
            series1.getData().remove(0);
        }
    }

    public void setBrakePosition(Map<String, Integer> brakeValues) {
        Date now = new Date();
        series2.getData().add(new XYChart.Data(simpleDateFormat.format(now), brakeValues.get("after")));
        if (series2.getData().size() > 250){
            series2.getData().remove(0);
        }
    }

    public void setClutchPosition(Map<String, Integer> clutchValues) {
        Date now = new Date();
        series3.getData().add(new XYChart.Data(simpleDateFormat.format(now), clutchValues.get("after")));
        if (series3.getData().size() > 250){
            series3.getData().remove(0);
        }
    }


    public void initialize() {


        timexAxis.setLabel("Input/s");
        timexAxis.setAnimated(false); // axis animations are removed
//        timexAxis.setForceZeroInRange(false);

        timeyAxis.setForceZeroInRange(false);
        timeyAxis.setAutoRanging(false);
        timeyAxis.setLowerBound(0);
        timeyAxis.setUpperBound(100);
        timeyAxis.setTickUnit(20);

        timeChart.getData().addAll(series1, series2, series3);
        timeChart.setAnimated(false); // disable animations
        timeChart.setLegendVisible(false);
        timeChart.getXAxis().setTickLabelsVisible(false);
        timeChart.getXAxis().setTickMarkVisible(false);
        timeChart.getXAxis().setTickLength(20);

    }

}
