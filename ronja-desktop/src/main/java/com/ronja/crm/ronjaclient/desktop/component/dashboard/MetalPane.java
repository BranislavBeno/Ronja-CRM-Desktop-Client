package com.ronja.crm.ronjaclient.desktop.component.dashboard;

import com.ronja.crm.ronjaclient.desktop.component.internationalization.I18nUtils;
import com.ronja.crm.ronjaclient.locale.i18n.I18N;
import com.ronja.crm.ronjaclient.service.domain.MetalData;
import com.ronja.crm.ronjaclient.service.service.MetalDataService;
import com.ronja.crm.ronjaclient.service.util.DateTimeUtil;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class MetalPane extends VBox {

    private static final int LEFT = 25;
    private static final int TOP_RIGHT_BOTTOM_LEFT = 10;

    private final int dailyLimit;
    private final int weeklyLimit;
    private final int monthlyLimit;
    private final MetalDataService dataService;
    private MetalResource metalResource;

    public MetalPane(int dailyLimit, int weeklyLimit, int monthlyLimit, MetalDataService dataService) {
        this.dailyLimit = dailyLimit;
        this.weeklyLimit = weeklyLimit;
        this.monthlyLimit = monthlyLimit;
        this.dataService = Objects.requireNonNull(dataService);

        setUpPane();
        setPadding(new Insets(TOP_RIGHT_BOTTOM_LEFT));
        setSpacing(2);
    }

    public void setUpPane() {
        this.metalResource = new MetalResource(dailyLimit, weeklyLimit, monthlyLimit, dataService.fetchData());
        getChildren().clear();
        getChildren().addAll(setUpTitle(), setUpLatestPrices(), setUpChartTabs());
    }

    private Label setUpTitle() {
        var title = I18nUtils.labelForValue("metal.label.current.prices");
        title.setPadding(new Insets(0, 0, 0, LEFT));

        return title;
    }

    private GridPane setUpLatestPrices() {
        var prices = new GridPane();
        prices.setPadding(new Insets(0, 0, 0, LEFT));
        metalResource.getLatestData().ifPresent(m -> addLatestPrices(prices, m));

        return prices;
    }

    private void addLatestPrices(GridPane prices, MetalData m) {
        prices.addRow(0, new Separator(), new Separator());
        prices.addRow(1, I18nUtils.labelForValue("metal.at.date"),
                new Label(m.getFetched().format(DateTimeUtil.DATE_TIME_FORMATTER)));
        prices.addRow(2, new Separator(), new Separator());
        prices.addRow(3, new Label(MetalType.ALUMINIUM + ": "), new Label(m.getAluminiumPrice()));
        prices.addRow(4, new Label(MetalType.LEAD + ": "), new Label(m.getLeadPrice()));
        prices.addRow(5, new Label(MetalType.COPPER + ": "), new Label(m.getCopperPrice()));
        prices.addRow(6, new Separator(), new Separator());
    }

    private TabPane setUpChartTabs() {
        Tab dailyTab = createTab(I18N.get("metal.tab.daily"), metalResource::getDailyData);
        Tab weeklyTab = createTab(I18N.get("metal.tab.weekly"), metalResource::getWeeklyData);
        Tab monthlyTab = createTab(I18N.get("metal.tab.monthly"), metalResource::getMonthlyData);

        TabPane pane = new TabPane();
        pane.getTabs().addAll(dailyTab, weeklyTab, monthlyTab);

        return pane;
    }

    private Tab createTab(String caption, Supplier<List<MetalData>> supplier) {
        Tab tab = new Tab(caption);
        LineChart<String, Number> chart = setUpChart(supplier);
        tab.setContent(chart);
        tab.setClosable(false);
        return tab;
    }

    private LineChart<String, Number> setUpChart(Supplier<List<MetalData>> supplier) {
        LineChart<String, Number> lineChart = createEmptyChart();
        lineChart.setPadding(new Insets(TOP_RIGHT_BOTTOM_LEFT));
        lineChart.setLegendSide(Side.TOP);
        lineChart.getData().add(fillChart(MetalType.ALUMINIUM, supplier));
        lineChart.getData().add(fillChart(MetalType.COPPER, supplier));
        lineChart.getData().add(fillChart(MetalType.LEAD, supplier));

        return lineChart;
    }

    private XYChart.Series<String, Number> fillChart(MetalType type, Supplier<List<MetalData>> supplier) {
        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        dataSeries.setName(type.toString());
        List<MetalData> metalDataArray = supplier.get();

        for (MetalData data : metalDataArray) {
            String date = data.getFetched().format(DateTimeUtil.DATE_TIME_FORMATTER);
            BigDecimal price = switch (type) {
                case ALUMINIUM -> data.getAluminum();
                case LEAD -> data.getLead();
                case COPPER -> data.getCopper();
            };
            XYChart.Data<String, Number> chartData = new XYChart.Data<>(date, price);
            dataSeries.getData().add(chartData);
        }

        return dataSeries;
    }

    private LineChart<String, Number> createEmptyChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        return new LineChart<>(xAxis, yAxis);
    }

    private enum MetalType {
        ALUMINIUM("metal.type.aluminium"),
        COPPER("metal.type.copper"),
        LEAD("metal.type.lead");

        private final String key;

        MetalType(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return I18N.get(key);
        }
    }
}