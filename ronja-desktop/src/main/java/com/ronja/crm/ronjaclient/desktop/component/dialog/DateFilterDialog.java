package com.ronja.crm.ronjaclient.desktop.component.dialog;

import com.ronja.crm.ronjaclient.desktop.component.representative.DateRecord;
import com.ronja.crm.ronjaclient.service.util.DateTimeUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.LocalDateStringConverter;

import java.time.LocalDate;
import java.util.Objects;

public class DateFilterDialog extends Dialog<DateRecord> {

    public final DatePicker startDatePicker;
    public final DatePicker endDatePicker;

    public DateFilterDialog(DateRecord dateRecord) {
        this(dateRecord.startDate(), dateRecord.endDate());
    }

    private DateFilterDialog(LocalDate startDate, LocalDate endDate) {
        startDatePicker = new DatePicker(Objects.requireNonNullElse(startDate, LocalDate.now()));
        startDatePicker.setConverter(
                new LocalDateStringConverter(DateTimeUtil.DATE_TIME_FORMATTER, DateTimeUtil.DATE_TIME_FORMATTER));
        endDatePicker = new DatePicker(Objects.requireNonNullElse(endDate, LocalDate.now()));
        endDatePicker.setConverter(
                new LocalDateStringConverter(DateTimeUtil.DATE_TIME_FORMATTER, DateTimeUtil.DATE_TIME_FORMATTER));

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER_LEFT);
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        VBox.setVgrow(gridPane, Priority.NEVER);
        gridPane.addRow(0, new Label("Od:"), new Label("Do:"));
        gridPane.addRow(1, startDatePicker, endDatePicker);

        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);
        getDialogPane().setContent(gridPane);

        // Create an event filter that consumes the action if the text is empty
        EventHandler<ActionEvent> filter = event -> {
            if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
                event.consume();
                Dialogs.showErrorMessage("Chyba", "Dátum 'od' nesmie byť neskorší ako 'do'!");
            }
        };

        // Set the button types.
        this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) this.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, filter);

        // Convert the result to a contact when the ok button is clicked.
        this.setResultConverter(button -> {
            if (button.equals(ButtonType.OK)) {
                return new DateRecord(startDatePicker.getValue(), endDatePicker.getValue());
            }
            return null;
        });
    }
}