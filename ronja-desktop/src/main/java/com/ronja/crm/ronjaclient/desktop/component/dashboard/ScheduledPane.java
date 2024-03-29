package com.ronja.crm.ronjaclient.desktop.component.dashboard;

import com.ronja.crm.ronjaclient.locale.i18n.I18N;
import com.ronja.crm.ronjaclient.service.clientapi.RepresentativeWebClient;
import com.ronja.crm.ronjaclient.service.domain.RonjaDate;
import com.ronja.crm.ronjaclient.service.domain.Scheduled;
import javafx.geometry.Insets;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.*;
import java.util.stream.Collectors;

public class ScheduledPane extends VBox {

    private final RepresentativeWebClient webClient;
    private final TreeView<String> scheduledTree;

    public ScheduledPane(RepresentativeWebClient webClient) {
        this.webClient = webClient;

        scheduledTree = new TreeView<>();
        setUpPane();
        VBox.setVgrow(scheduledTree, Priority.ALWAYS);

        getChildren().addAll(scheduledTree);
        setPadding(new Insets(5));
        setSpacing(5);
        setMaxWidth(400);
    }

    public void setUpPane() {
        TreeItem<String> rootItem = createTreeItems();
        rootItem.setExpanded(true);
        scheduledTree.setRoot(rootItem);
    }

    private TreeItem<String> createTreeItems() {
        TreeItem<String> rootItem = new TreeItem<>(I18N.get("label.scheduled.meetings") + ":");
        fetchScheduledRepresentatives(webClient)
                .entrySet()
                .stream()
                .map(this::addTreeItem)
                .forEach(rootItem.getChildren()::add);

        return rootItem;
    }

    private TreeItem<String> addTreeItem(Map.Entry<String, List<Scheduled>> entry) {
        TreeItem<String> item = toDateItem(entry);
        entry.getValue()
                .stream()
                .map(this::toScheduledItem)
                .forEach(item.getChildren()::add);

        return item;
    }

    private TreeItem<String> toDateItem(Map.Entry<String, List<Scheduled>> entry) {
        return new TreeItem<>("%s (%d)".formatted(entry.getKey(), entry.getValue().size()));
    }

    private TreeItem<String> toScheduledItem(Scheduled v) {
        String company = v.getCustomerName() != null ? " - " + v.getCustomerName() : "";
        return new TreeItem<>("%s %s%s".formatted(v.getFirstName(), v.getLastName(), company));
    }

    private Map<String, List<Scheduled>> fetchScheduledRepresentatives(RepresentativeWebClient webClient) {
        Scheduled[] scheduled = Objects.requireNonNull(webClient.fetchScheduledRepresentatives(14).block());

        return Arrays.stream(scheduled)
                .collect(Collectors.groupingBy(this::dateToString, TreeMap::new, Collectors.toList()));
    }

    private String dateToString(Scheduled representative) {
        return new RonjaDate(representative.getScheduledVisit()).toString();
    }
}
