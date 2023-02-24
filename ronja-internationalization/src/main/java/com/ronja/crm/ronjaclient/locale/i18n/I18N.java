package com.ronja.crm.ronjaclient.locale.i18n;

import java.text.MessageFormat;
import java.util.*;

public final class I18N {

    private static Locale locale;

    static {
        locale = getDefaultLocale();
    }

    private I18N() {
    }

    public static List<Locale> getSupportedLocales() {
        return new ArrayList<>(Arrays.asList(Locale.ENGLISH, Locale.of("sk", "SK")));
    }

    public static Locale getDefaultLocale() {
        Locale sysDefault = Locale.getDefault();
        return getSupportedLocales().contains(sysDefault) ? sysDefault : Locale.ENGLISH;
    }

    public static Locale getLocale() {
        return locale;
    }

    public static void setLocale(Locale locale) {
        I18N.locale = locale;
        Locale.setDefault(locale);
    }

    public static String get(final String key, final Object... args) {
        ResourceBundle bundle = ResourceBundle.getBundle("language/messages", getLocale());
        return MessageFormat.format(bundle.getString(key), args);
    }
}