package com.ronja.crm.ronjaclient.desktop.component.representative;

import com.ronja.crm.ronjaclient.desktop.component.customer.FetchException;
import com.ronja.crm.ronjaclient.desktop.component.util.TableViewUtil;
import com.ronja.crm.ronjaclient.service.clientapi.RepresentativeWebClient;
import com.ronja.crm.ronjaclient.service.domain.Representative;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.tableview2.FilteredTableView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class RepresentativeTableView extends VBox {

  @Value("${client.representative.base-url}")
  String baseUrl;
  @Autowired
  private final RepresentativeWebClient representativeWebClient;

  private final ObservableList<RepresentativeTableItem> tableItems;
  private final FilteredTableView<RepresentativeTableItem> tableView;

  public RepresentativeTableView(RepresentativeWebClient representativeWebClient) {
    this.representativeWebClient = Objects.requireNonNull(representativeWebClient);

    tableView = new FilteredTableView<>();
    getChildren().add(tableView);

    tableItems = FXCollections.observableArrayList();
    addItems();
    setUpTableView();
    FilteredTableView.configureForFiltering(tableView, tableItems);
  }

  public void refreshItems() {
    tableItems.clear();
    addItems();
  }

  private void addItems() {
    Platform.runLater(() -> fetchRepresentatives().forEach(this::addItem));
  }

  private Stream<Representative> fetchRepresentatives() {
    try {
      Representative[] representatives = Objects.requireNonNull(representativeWebClient.fetchAllRepresentatives().block());
      return Arrays.stream(representatives).sorted(Comparator.comparing(Representative::getLastName));
    } catch (Exception e) {
      throw new FetchException("""
          Nepodarilo sa získať dáta o reprezentantoch.
          Preverte spojenie so serverom.""", e);
    }
  }

  private void addItem(Representative representative) {
    var item = new RepresentativeTableItem(representative);
    addItem(item);
  }

  public void addItem(RepresentativeTableItem item) {
    tableItems.add(item);
  }

  private void setUpTableView() {
    TableViewUtil.addColumn("Meno",
        Pos.CENTER_LEFT, tableView, String.class, RepresentativeTableItem::firstNameProperty);
    TableViewUtil.addColumn("Priezvisko", tableView, String.class, RepresentativeTableItem::lastNameProperty);
    TableViewUtil.addColumn("Pozícia", tableView, String.class, RepresentativeTableItem::positionProperty);
    TableViewUtil.addColumn("Región", tableView, String.class, RepresentativeTableItem::regionProperty);

    VBox.setVgrow(tableView, Priority.ALWAYS);
  }
}
