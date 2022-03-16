package com.ronja.crm.ronjaclient.desktop.component.dashboard;

import com.ronja.crm.ronjaclient.desktop.component.common.FetchException;
import com.ronja.crm.ronjaclient.service.clientapi.MetalDataWebClient;
import com.ronja.crm.ronjaclient.service.domain.MetalData;
import com.ronja.crm.ronjaclient.service.util.DateTimeUtil;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

public class MetalPane extends VBox {

    private static final int LEFT = 25;
    private static final int TOP_RIGHT_BOTTOM_LEFT = 10;
    private final MetalDataWebClient webClient;

    public MetalPane(MetalDataWebClient webClient) {
        this.webClient = webClient;

        setUpPane();
        setPadding(new Insets(TOP_RIGHT_BOTTOM_LEFT));
        setSpacing(2);
    }

    public void setUpPane() {
        getChildren().clear();
        getChildren().addAll(setUpTitle(), setUpLatestPrices(), setUpChart());
    }

    private Label setUpTitle() {
        var title = new Label("Ceny podľa LME:");
        title.setPadding(new Insets(0, 0, 0, LEFT));

        return title;
    }

    private GridPane setUpLatestPrices() {
        var prices = new GridPane();
        prices.setPadding(new Insets(0, 0, 0, LEFT));

        fetchMetalData()
                .reduce((first, second) -> second)
                .ifPresent(m -> addLatestPrices(prices, m));

        return prices;
    }

    private void addLatestPrices(GridPane prices, MetalData m) {
        prices.addRow(0, new Separator(), new Separator());
        prices.addRow(1, new Label("Zo dňa: "), new Label(m.getFetched().format(DateTimeUtil.DATE_TIME_FORMATTER)));
        prices.addRow(2, new Separator(), new Separator());
        prices.addRow(3, new Label(MetalType.ALUMINIUM.getTitle() + ": "), new Label(m.getAluminiumPrice()));
        prices.addRow(4, new Label(MetalType.LEAD.getTitle() + ": "), new Label(m.getLeadPrice()));
        prices.addRow(5, new Label(MetalType.COPPER.getTitle() + ": "), new Label(m.getCopperPrice()));
        prices.addRow(6, new Separator(), new Separator());
    }

    private Stream<MetalData> fetchMetalData() {
        try {
            MetalData[] metalData = Objects.requireNonNull(webClient.fetchMetalData().block());
            return Arrays.stream(metalData).sorted(Comparator.comparing(MetalData::getFetched));
        } catch (Exception e) {
            throw new FetchException("""
                    Nepodarilo sa získať dáta o cene kovov.
                    Preverte spojenie so serverom.""", e);
        }
    }

    private LineChart<String, Number> setUpChart() {
        LineChart<String, Number> lineChart = createEmptyChart();
        lineChart.setPadding(new Insets(TOP_RIGHT_BOTTOM_LEFT));
        lineChart.setLegendSide(Side.TOP);
        lineChart.getData().add(fillChart(MetalType.ALUMINIUM));
        lineChart.getData().add(fillChart(MetalType.COPPER));
        lineChart.getData().add(fillChart(MetalType.LEAD));

        return lineChart;
    }

    private XYChart.Series<String, Number> fillChart(MetalType type) {
        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        dataSeries.setName(type.getTitle());
        MetalData[] metalDataArray = webClient.fetchMetalData().block();

        if (metalDataArray != null) {
            for (MetalData metalData : metalDataArray) {
                String date = metalData.getFetched().format(DateTimeUtil.DATE_TIME_FORMATTER);
                BigDecimal price = switch (type) {
                    case ALUMINIUM -> metalData.getAluminum();
                    case LEAD -> metalData.getLead();
                    case COPPER -> metalData.getCopper();
                };
                XYChart.Data<String, Number> chartData = new XYChart.Data<>(date, price);
                dataSeries.getData().add(chartData);
            }
        }

        return dataSeries;
    }

    private LineChart<String, Number> createEmptyChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        return new LineChart<>(xAxis, yAxis);
    }

    enum MetalType {
        ALUMINIUM("Hliník"), COPPER("Meď"), LEAD("Olovo");

        private final String title;

        MetalType(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }
}
