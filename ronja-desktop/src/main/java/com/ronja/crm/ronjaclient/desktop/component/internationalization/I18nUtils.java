package com.ronja.crm.ronjaclient.desktop.component.internationalization;

import com.ronja.crm.ronjaclient.locale.i18n.I18N;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;

import java.util.Locale;


public class I18nUtils {

    private static final ObjectProperty<Locale> LOCALE;

    static {
        LOCALE = new SimpleObjectProperty<>(I18N.getDefaultLocale());
        LOCALE.addListener((observable, oldValue, newValue) -> I18N.setLocale(newValue));
    }

    private I18nUtils() {
    }

    public static StringBinding createStringBinding(final String key, Object... args) {
        return Bindings.createStringBinding(() -> I18N.get(key, args), LOCALE);
    }

    public static Label labelForValue(final String key, final Object... args) {
        Label label = new Label();
        label.textProperty().bind(createStringBinding(key, args));
        return label;
    }
}
