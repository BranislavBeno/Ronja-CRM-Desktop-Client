package com.ronja.crm.ronjaclient.desktop.component.internationalization;

import com.ronja.crm.ronjaclient.locale.i18n.I18N;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

import java.util.Locale;


public class I18nUtils {

    private static final ObjectProperty<Locale> LOCALE;

    static {
        LOCALE = new SimpleObjectProperty<>(I18N.getDefaultLocale());
        LOCALE.addListener((_, _, newValue) -> I18N.setLocale(newValue));
    }

    private I18nUtils() {
    }

    public static StringBinding createStringBinding(String key, Object... args) {
        return Bindings.createStringBinding(() -> I18N.get(key, args), LOCALE);
    }

    public static Label labelForValue(String key) {
        Label label = new Label();
        label.textProperty().bind(createStringBinding(key));
        return label;
    }
    public static MenuItem menuItemForValue(String key) {
        MenuItem item = new MenuItem();
        item.textProperty().bind(createStringBinding(key));
        return item;
    }

    public static Button menuItemForButton(String key) {
        Button item = new Button();
        item.textProperty().bind(createStringBinding(key));
        return item;
    }

    public static Alert alertForValue(Alert.AlertType type, String key) {
        Alert alert = new Alert(type);
        alert.titleProperty().bind(createStringBinding(key));
        return alert;
    }
}
