package ru.thecop.smtm2.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PreferenceUtils {

    //todo move to strings.xml
    private static final String PREF_DATABASE_INITIALIZED = "pref_database_initialized";
    private static final String PREF_SMS_PARSE_STOP_WORDS = "pref_sms_parse_stop_words";
    private static final String PREF_SMS_PARSE_GO_WORDS = "pref_sms_parse_go_words";
    public static final String PREF_HAS_NEW_SMS_PARSED = "pref_has_new_sms_parsed";

    public static boolean isDatabaseInitialized(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_DATABASE_INITIALIZED, false);
    }

    public static void setDatabaseInitialized(Context context, boolean databaseInitialized) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(PREF_DATABASE_INITIALIZED, databaseInitialized);
        editor.apply();
    }

    @SuppressWarnings("unchecked")
    public static Set<String> getSmsParseStopWords(Context context) {
        //todo implement
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getStringSet(PREF_SMS_PARSE_STOP_WORDS, Collections.EMPTY_SET);
    }

    public static void setSmsParseStopWords(Context context, Set<String> stopWords) {
        Set<String> lowerCased = new HashSet<>(stopWords.size());
        for (String stopWord : stopWords) {
            lowerCased.add(stopWord.toLowerCase());
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(PREF_SMS_PARSE_STOP_WORDS, lowerCased);
        editor.apply();
    }

    @SuppressWarnings("unchecked")
    public static Set<String> getSmsParseGoWords(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getStringSet(PREF_SMS_PARSE_GO_WORDS, Collections.EMPTY_SET);
    }

    public static void setSmsParseGoWords(Context context, Set<String> goWords) {
        Set<String> lowerCased = new HashSet<>(goWords.size());
        for (String stopWord : goWords) {
            lowerCased.add(stopWord.toLowerCase());
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(PREF_SMS_PARSE_GO_WORDS, lowerCased);
        editor.apply();
    }

    public static void setHasNewSmsParsed(Context context, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(PREF_HAS_NEW_SMS_PARSED, value);
        editor.apply();
    }

    public static boolean getHasNewSmsParsed(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_HAS_NEW_SMS_PARSED, false);
    }

    public static void registerListener(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unRegisterListener(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
