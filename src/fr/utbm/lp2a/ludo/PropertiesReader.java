package fr.utbm.lp2a.ludo;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class PropertiesReader {

    private static final ResourceBundle STRING_RES_BUNDLE = ResourceBundle.getBundle("fr.utbm.lp2a.ludo.strings");

    public static String getString(String key) {
        try {
            return STRING_RES_BUNDLE.getString(key);
        }
        catch (MissingResourceException e) {
            return key;
        }
    }
}