package org.example.bulletgraph;

import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.geometry.*;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Quick and dirty implementation of a <a href=\"https://en.wikipedia.org/wiki/Bullet_graph\">bullet chart</a>.
 * @author Fabrice Bouyé
 */
public class BulletGraph extends Region {

    private static final PseudoClass VERTICAL_PSEUDO_CLASS = PseudoClass.getPseudoClass("vertical"); // NOI18N.
    private static final String USER_AGENT_STYLE_SHEET = "BulletChart.css"; // NOI18N.

    private final NumberAxis axis = new NumberAxis(0d, 100d, 100d);
    private final Region performanceMeasureMarker = new Region();
    private final Region comparativeMeasureMarker = new Region();
    private final Region lowerCalibrationMarker = new Region();
    private final Region higherCalibrationMarker = new Region();
    private final Region lowerDeadzoneMarker = new Region();
    private final Region higherDeadzoneMarker = new Region();
//    private final Group quantitativeScaleGroup = new Group();
    protected final Region plotArea = new Region() {
        {
//            getChildren().add(quantitativeScaleGroup);
            getChildren().add(performanceMeasureMarker);
            getChildren().add(comparativeMeasureMarker);
            getChildren().add(lowerCalibrationMarker);
            getChildren().add(higherCalibrationMarker);
            getChildren().add(lowerDeadzoneMarker);
            getChildren().add(higherDeadzoneMarker);
        }

        @Override
        protected void layoutChildren() {
            layoutPlotChildren();
        }
    };
    private final Text titleLabel = new Text();
    private final Text descriptionLabel = new Text();
    private final TextFlow titleFlow = new TextFlow(titleLabel, new Text("\n"), descriptionLabel); // NOI18N.
    private final Tooltip performanceMeasureTip = new Tooltip();
    private final Tooltip comparativeMeasureTip = new Tooltip();

    private final Tooltip lowerCalibrationTip = new Tooltip();
    private final Tooltip higherCalibrationTip = new Tooltip();
    private final Tooltip lowerDeadzoneTip = new Tooltip();
    private final Tooltip higherDeadzoneTip = new Tooltip();



    /**
     * Creates a new instance.
     */
    public BulletGraph() {
        super();
        setId("bulletChart"); // NOI18N.
        getStyleClass().add("bullet-chart"); // NOI18N.
        //
        setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        //
        axis.setSide(Side.BOTTOM);
        getChildren().add(axis);
        //
        performanceMeasureMarker.getStyleClass().add("performance-measure-marker"); // NOI18N.
        performanceMeasureTip.textProperty().bind(performanceMeasureProperty().asString());
        Tooltip.install(performanceMeasureMarker, performanceMeasureTip);
        //
        comparativeMeasureMarker.setVisible(false);
        comparativeMeasureMarker.getStyleClass().add("comparative-measure-marker"); // NOI18N.
        comparativeMeasureTip.textProperty().bind(comparativeMeasureProperty().asString());
        Tooltip.install(comparativeMeasureMarker, comparativeMeasureTip);

        //
        lowerCalibrationMarker.setVisible(false);
        lowerCalibrationMarker.getStyleClass().add("lower-calibration-marker"); // NOI18N.
        lowerCalibrationTip.textProperty().bind(lowerCalibrationProperty().asString());
        Tooltip.install(lowerCalibrationMarker, lowerCalibrationTip);
        //
        higherCalibrationMarker.setVisible(false);
        higherCalibrationMarker.getStyleClass().add("higher-calibration-marker"); // NOI18N.
        higherCalibrationTip.textProperty().bind(higherCalibrationProperty().asString());
        Tooltip.install(higherCalibrationMarker, higherCalibrationTip);
        //
        lowerDeadzoneMarker.setVisible(false);
        lowerDeadzoneMarker.getStyleClass().add("lower-deadzone-marker"); // NOI18N.
        lowerDeadzoneTip.textProperty().bind(lowerDeadzoneProperty().asString());
        Tooltip.install(lowerDeadzoneMarker, lowerDeadzoneTip);
        //
        higherDeadzoneMarker.setVisible(false);
        higherDeadzoneMarker.getStyleClass().add("higher-deadzone-marker"); // NOI18N.
        higherDeadzoneTip.textProperty().bind(higherDeadzoneProperty().asString());
        Tooltip.install(higherDeadzoneMarker, higherDeadzoneTip);
        //

        plotArea.getStyleClass().add("plot-area"); // NOI18N.
        getChildren().add(plotArea);
        //
        getChildren().add(titleFlow);
        titleFlow.getStyleClass().add("title-flow"); // NOI18N.
        titleLabel.textProperty().bind(titleProperty());
        titleLabel.getStyleClass().add("title"); // NOI18N.
        descriptionLabel.textProperty().bind(descriptionProperty());
        descriptionLabel.getStyleClass().add("description"); // NOI18N.
        //
        axis.lowerBoundProperty().addListener(layoutRequestListener);
        axis.upperBoundProperty().addListener(layoutRequestListener);
        comparativeMeasureProperty().addListener(layoutRequestListener);

        lowerCalibrationProperty().addListener(layoutRequestListener);
        higherCalibrationProperty().addListener(layoutRequestListener);
        lowerDeadzoneProperty().addListener(layoutRequestListener);
        higherDeadzoneProperty().addListener(layoutRequestListener);

        performanceMeasureProperty().addListener(layoutRequestListener);
        orientationProperty().addListener(layoutRequestListener);
        titleProperty().addListener(layoutRequestListener);
        descriptionProperty().addListener(layoutRequestListener);
        titleAxisGapProperty().addListener(layoutRequestListener);
//        getQuantitativeScale().addListener((Change<? extends Double> change) -> {
//            resetQuantitativeShapes();
//            prepareForLayout();
//        });
//        //
//        resetQuantitativeShapes();
        prepareForLayout();
    }

    @Override
    public String getUserAgentStylesheet() {
        return getClass().getResource(USER_AGENT_STYLE_SHEET).toExternalForm();
    }

    /**
     * Called when values are invalidated.
     * <br>Call for a relayout.
     */
    private final InvalidationListener layoutRequestListener = observable -> prepareForLayout();

//    private void resetQuantitativeShapes() {
//        // Clear old shapes.
//        quantitativeScaleGroup.getChildren().clear();
//        // Create new ones.
//        IntStream.range(0, quantitativeScale.size())
//                .forEach(index -> {
//                    Region region = new Region();
//                    final String style = String.format("quantitative-scale%d", index + 1); // NOI18N.
//                    region.getStyleClass().add(style);
//                    quantitativeScaleGroup.getChildren().add(region);
//                });
//    }

    private void prepareForLayout() {
        final Orientation orientation = getOrientation();
        final boolean isVertical = orientation == Orientation.VERTICAL;
        pseudoClassStateChanged(VERTICAL_PSEUDO_CLASS, isVertical);
        axis.setSide(isVertical ? Side.LEFT : Side.BOTTOM);
        if (!maxWidthProperty().isBound()) {
            setMaxWidth(isVertical ? USE_PREF_SIZE : Double.MAX_VALUE);
        }
        if (!maxHeightProperty().isBound()) {
            setMaxHeight(isVertical ? Double.MAX_VALUE : USE_PREF_SIZE);
        }
        requestLayout();
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        final double width = getWidth();
        final double height = getHeight();
        final Insets insets = getInsets();
        final double areaX = insets.getLeft();
        final double areaY = insets.getTop();
        final double areaW = Math.max(0, width - (insets.getLeft() + insets.getRight()));
        final double areaH = Math.max(0, height - (insets.getTop() + insets.getBottom()));
        layoutChartChildren(areaX, areaY, areaW, areaH);
    }

    /**
     * Layout chart in given area.
     * @param areaX Area's X coordinate.
     * @param areaY Area's Y coordinate.
     * @param areaW Area's width.
     * @param areaH Area's height.r
     */
    protected void layoutChartChildren(double areaX, double areaY, double areaW, double areaH) {
        final Orientation orientation = getOrientation();
        final double titleAxisGap = Math.max(0, getTitleAxisGap());
        switch (orientation) {
            case VERTICAL: {
                final double titleW = Math.min(areaW, titleFlow.getWidth());
                final double titleH = titleFlow.prefHeight(titleW);
                final double titleY = areaY;
                final double axisX = areaX;
                final double axisY = titleY + titleH + titleAxisGap;
                final double axisW = axis.getWidth();
                final double axisH = areaH - axisY;
                layoutInArea(axis, axisX, axisY, axisW, axisH, 0, HPos.LEFT, VPos.TOP);
                final double plotChildrenX = axisX + axisW;
                final double plotChildrenY = axisY;
                final double plotChildrenW = areaW - axisW;
                final double plotChildrenH = axisH;
                layoutInArea(plotArea, plotChildrenX, plotChildrenY, plotChildrenW, plotChildrenH, 0, HPos.LEFT, VPos.TOP);
                final double titleX = plotChildrenX + (plotChildrenW - titleW) / 2;
                layoutInArea(titleFlow, titleX, titleY, titleW, titleH, 0, HPos.LEFT, VPos.TOP);
            }
            break;
            case HORIZONTAL:
            default: {
                final double titleW = Math.min(areaW / 2, titleFlow.getWidth());
                final double titleH = titleFlow.prefHeight(titleW);
                final double titleX = areaX;
                final double titleY = areaY + (areaH - titleH) / 2;
                layoutInArea(titleFlow, titleX, titleY, titleW, titleH, 0, HPos.LEFT, VPos.TOP);
                final double axisX = titleX + titleW + titleAxisGap;
                final double axisW = areaW - axisX;
                final double axisH = axis.getHeight();
                final double axisY = areaY + areaH - axisH;
                layoutInArea(axis, axisX, axisY, axisW, axisH, 0, HPos.LEFT, VPos.TOP);
                final double plotChildrenX = axisX;
                final double plotChildrenY = areaY;
                final double plotChildrenW = axisW;
                final double plotChildrenH = areaH - axisH;
                layoutInArea(plotArea, plotChildrenX, plotChildrenY, plotChildrenW, plotChildrenH, 0, HPos.LEFT, VPos.TOP);
            }
        }
        layoutPlotChildren();
    }

    /**
     * Layout plot children in plot area.
     */
    protected void layoutPlotChildren() {
        final Orientation orientation = getOrientation();
        final double width = plotArea.getWidth();
        final double height = plotArea.getHeight();
        final double lowerBound = axis.getLowerBound();
        final double upperBound = axis.getUpperBound();
        final double performanceMeasure = getPerformanceMeasure();
        final double comparativeMeasure = getComparativeMeasure();
        final double lowerCalibration = getLowerCalibration();
        final double higherCalibration = getHigherCalibration();
        final double lowerDeadzone = getLowerDeadzone();
        final double higherDeadzone = getHigherDeadzone();
        switch (orientation) {
            case VERTICAL: {
//                IntStream.range(0, quantitativeScale.size())
//                        .forEach(index -> {
//                            final Region region = (Region) quantitativeScaleGroup.getChildren().get(index);
//                            double stop = quantitativeScale.get(index);
//                            double previousStop = (index == 0) ? 0 : quantitativeScale.get(index - 1);
//                            double w = width;
//                            double h = height * (stop - previousStop);
//                            double x = 0;
//                            double y = height - height * stop;
//                            region.relocate(x, y);
//                            region.setMinSize(w, h);
////                            layoutInArea(region, x, y, w, h, 0, HPos.LEFT, VPos.TOP);
//                        });
                double performanceW = performanceMeasureMarker.getWidth();
                double performanceH = height * (performanceMeasure - lowerBound) / (upperBound - lowerBound);
                double performanceX = (width - performanceW) / 2;
                double performanceY = height - performanceH;
                layoutInArea(performanceMeasureMarker, performanceX, performanceY, performanceW, performanceH, 0, HPos.LEFT, VPos.TOP);

                double comparativeW = comparativeMeasureMarker.getWidth();
                double comparativeH = comparativeMeasureMarker.getHeight();
                double comparativeX = (width - comparativeW) / 2;
                double comparativeY = height - height * (comparativeMeasure - lowerBound) / (upperBound - lowerBound) - comparativeH / 2;
                layoutInArea(comparativeMeasureMarker, comparativeX, comparativeY, comparativeW, comparativeH, 0, HPos.LEFT, VPos.TOP);


                double lowerCalibrationW = lowerCalibrationMarker.getWidth();
                double lowerCalibrationH = lowerCalibrationMarker.getHeight();
                double lowerCalibrationX = (width - lowerCalibrationW) / 2;
                double lowerCalibrationY = height - height * (lowerCalibration - lowerBound) / (upperBound - lowerBound) - lowerCalibrationH / 2;
                layoutInArea(lowerCalibrationMarker, lowerCalibrationX, lowerCalibrationY, lowerCalibrationW, lowerCalibrationH, 0, HPos.LEFT, VPos.TOP);


                double higherCalibrationW = higherCalibrationMarker.getWidth();
                double higherCalibrationH = higherCalibrationMarker.getHeight();
                double higherCalibrationX = (width - higherCalibrationW) / 2;
                double higherCalibrationY = height - height * (higherCalibration - lowerBound) / (upperBound - lowerBound) - higherCalibrationH / 2;
                layoutInArea(higherCalibrationMarker, higherCalibrationX, higherCalibrationY, higherCalibrationW, higherCalibrationH, 0, HPos.LEFT, VPos.TOP);

                double lowerDeadzoneW = lowerDeadzoneMarker.getWidth();
                double lowerDeadzoneH = lowerDeadzoneMarker.getHeight();
                double lowerDeadzoneX = (width - lowerDeadzoneW) / 2;
                double lowerDeadzoneY = height - height * (lowerDeadzone - lowerBound) / (upperBound - lowerBound) - lowerDeadzoneH / 2;
                layoutInArea(lowerDeadzoneMarker, lowerDeadzoneX, lowerDeadzoneY, lowerDeadzoneW, lowerDeadzoneH, 0, HPos.LEFT, VPos.TOP);


                double higherDeadzoneW = higherDeadzoneMarker.getWidth();
                double higherDeadzoneH = higherDeadzoneMarker.getHeight();
                double higherDeadzoneX = (width - higherDeadzoneW) / 2;
                double higherDeadzoneY = height - height * (higherDeadzone - lowerBound) / (upperBound - lowerBound) - higherDeadzoneH / 2;
                layoutInArea(higherDeadzoneMarker, higherDeadzoneX, higherDeadzoneY, higherDeadzoneW, higherDeadzoneH, 0, HPos.LEFT, VPos.TOP);

            }
            break;
            case HORIZONTAL:
            default: {
//                IntStream.range(0, quantitativeScale.size())
//                        .forEach(index -> {
//                            final Region region = (Region) quantitativeScaleGroup.getChildren().get(index);
//                            double stop = quantitativeScale.get(index);
//                            double previousStop = (index == 0) ? 0 : quantitativeScale.get(index - 1);
//                            double w = width * (stop - previousStop);
//                            double h = height;
//                            double x = width * previousStop;
//                            double y = 0;
//                            region.relocate(x, y);
//                            region.setMinSize(w, h);
////                            layoutInArea(region, x, y, w, h, 0, HPos.LEFT, VPos.TOP);
//                        });
                double performanceW = width * (performanceMeasure - lowerBound) / (upperBound - lowerBound);
                double performanceH = performanceMeasureMarker.getHeight();
                double performanceX = 0;
                double performanceY = (height - performanceH) / 2;
                layoutInArea(performanceMeasureMarker, performanceX, performanceY, performanceW, performanceH, 0, HPos.LEFT, VPos.TOP);
                
                double comparativeW = comparativeMeasureMarker.getWidth();
                double comparativeH = comparativeMeasureMarker.getHeight();
                double comparativeX = width * (comparativeMeasure - lowerBound) / (upperBound - lowerBound) - comparativeW / 2;
                double comparativeY = (height - comparativeH) / 2;
                layoutInArea(comparativeMeasureMarker, comparativeX, comparativeY, comparativeW, comparativeH, 0, HPos.LEFT, VPos.TOP);


                double lowerCalibrationW = lowerCalibrationMarker.getWidth();
                double lowerCalibrationH = lowerCalibrationMarker.getHeight();
                double lowerCalibrationX = width * (lowerCalibration - lowerBound) / (upperBound - lowerBound) - lowerCalibrationW / 2;
                double lowerCalibrationY = (height - lowerCalibrationH) / 2;
                layoutInArea(lowerCalibrationMarker, lowerCalibrationX, lowerCalibrationY, lowerCalibrationW, lowerCalibrationH, 0, HPos.LEFT, VPos.TOP);


                double higherCalibrationW = higherCalibrationMarker.getWidth();
                double higherCalibrationH = higherCalibrationMarker.getHeight();
                double higherCalibrationX = width * (higherCalibration - lowerBound) / (upperBound - lowerBound) - higherCalibrationW / 2;
                double higherCalibrationY = (height - higherCalibrationH) / 2;
                layoutInArea(higherCalibrationMarker, higherCalibrationX, higherCalibrationY, higherCalibrationW, higherCalibrationH, 0, HPos.LEFT, VPos.TOP);


                double lowerDeadzoneW = lowerDeadzoneMarker.getWidth();
                double lowerDeadzoneH = lowerDeadzoneMarker.getHeight();
                double lowerDeadzoneX = width * (lowerDeadzone - lowerBound) / (upperBound - lowerBound) - lowerDeadzoneW / 2;
                double lowerDeadzoneY = (height - lowerDeadzoneH) / 2;
                layoutInArea(lowerDeadzoneMarker, lowerDeadzoneX, lowerDeadzoneY, lowerDeadzoneW, lowerDeadzoneH, 0, HPos.LEFT, VPos.TOP);


                double higherDeadzoneW = higherDeadzoneMarker.getWidth();
                double higherDeadzoneH = higherDeadzoneMarker.getHeight();
                double higherDeadzoneX = width * (higherDeadzone - lowerBound) / (upperBound - lowerBound) - higherDeadzoneW / 2;
                double higherDeadzoneY = (height - higherDeadzoneH) / 2;
                layoutInArea(higherDeadzoneMarker, higherDeadzoneX, higherDeadzoneY, higherDeadzoneW, higherDeadzoneH, 0, HPos.LEFT, VPos.TOP);

            }
        }
    }

    public ValueAxis getAxis() {
        return axis;
    }

    private final DoubleProperty comparativeMeasure = new SimpleDoubleProperty(this, "comparativeMeasure", 0); // NOI18N.

    private final DoubleProperty lowerCalibration = new SimpleDoubleProperty(this, "lowerCalibration", 0); // NOI18N.
    private final DoubleProperty higherCalibration = new SimpleDoubleProperty(this, "higherCalibration", 0); // NOI18N.
    private final DoubleProperty lowerDeadzone = new SimpleDoubleProperty(this, "lowerDeadzone", 0); // NOI18N.
    private final DoubleProperty higherDeadzone = new SimpleDoubleProperty(this, "higherDeadzone", 0); // NOI18N.



    public final double getComparativeMeasure() {
        return comparativeMeasure.get();
    }

    public final void setComparativeMeasure(double value) {
        comparativeMeasureMarker.setVisible(true);
        comparativeMeasure.set(value);
    }


    public final double getLowerCalibration() {
        return lowerCalibration.get();
    }

    public final void setLowerCalibration(double value) {
        lowerCalibrationMarker.setVisible(true);
        lowerCalibration.set(value);
    }
    

    public final double getHigherCalibration() {
        return higherCalibration.get();
    }

    public final void setHigherCalibration(double value) {
        higherCalibrationMarker.setVisible(true);
        higherCalibration.set(value);
    }


    public final double getLowerDeadzone() {
        return lowerDeadzone.get();
    }

    public final void setLowerDeadzone(double value) {
        lowerDeadzoneMarker.setVisible(true);
        lowerDeadzone.set(value);
    }


    public final double getHigherDeadzone() {
        return higherDeadzone.get();
    }

    public final void setHigherDeadzone(double value) {
        higherDeadzoneMarker.setVisible(true);
        higherDeadzone.set(value);
    }
    

    public final void setLowerBound(double value) {
        axis.setLowerBound(value);
    }
    public final void setUpperBound(double value) {
        axis.setUpperBound(value);
    }

    public final void setTickUnit(double value) {
        axis.setTickUnit(value);
    }

    public final DoubleProperty comparativeMeasureProperty() {
        return comparativeMeasure;
    }


    public final DoubleProperty lowerCalibrationProperty() {
        return lowerCalibration;
    }

    public final DoubleProperty higherCalibrationProperty() {
        return higherCalibration;
    }


    public final DoubleProperty lowerDeadzoneProperty() {
        return lowerDeadzone;
    }

    public final DoubleProperty higherDeadzoneProperty() {
        return higherDeadzone;
    }


    private final DoubleProperty performanceMeasure = new SimpleDoubleProperty(this, "performanceMeasure", 0); // NOI18N.

    public final double getPerformanceMeasure() {
        return performanceMeasure.get();
    }

    public final void setPerformanceMeasure(double value) {
        performanceMeasure.set(value);
    }

    public final DoubleProperty performanceMeasureProperty() {
        return performanceMeasure;
    }

    private final ReadOnlyObjectWrapper<Orientation> orientation = new ReadOnlyObjectWrapper<>(this, "orientation", Orientation.HORIZONTAL); // NOI18N.

    public final Orientation getOrientation() {
        return orientation.get();
    }

    public final void setOrientation(Orientation value) {
        Orientation v = (value == null) ? Orientation.HORIZONTAL : value;
        orientation.set(v);
    }

    public final ReadOnlyObjectProperty<Orientation> orientationProperty() {
        return orientation.getReadOnlyProperty();
    }

    private final StringProperty title = new SimpleStringProperty(this, "title", null); // NOI18N.

    public final String getTitle() {
        return title.get();
    }

    public final void setTitle(String value) {
        title.set(value);
    }

    public final StringProperty titleProperty() {
        return title;
    }

    private final StringProperty description = new SimpleStringProperty(this, "description", null); // NOI18N.

    public final String getDescription() {
        return description.get();
    }

    public final void setDescription(String value) {
        description.set(value);
    }

    public final StringProperty descriptionProperty() {
        return description;
    }

    private final DoubleProperty titleAxisGap = new SimpleDoubleProperty(this, "titleAxisGap", 6); // NOI18N.

    public final double getTitleAxisGap() {
        return titleAxisGap.get();
    }

    public final void setTitleAxisGap(double value) {
        titleAxisGap.set(value);
    }

    public final DoubleProperty titleAxisGapProperty() {
        return titleAxisGap;
    }
//    private final ReadOnlyListWrapper<Double> quantitativeScale = new ReadOnlyListWrapper<>(this, "quantitativeScale", FXCollections.observableArrayList(0.75, 0.90, 1.0)); // NOI18N.
//
//    public final ObservableList<Double> getQuantitativeScale() {
//        return quantitativeScale.get();
//    }
//
//    public final ReadOnlyListProperty<Double> quantitativeScaleProperty() {
//        return quantitativeScale.getReadOnlyProperty();
//    }

}