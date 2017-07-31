package ru.thecop.smtm2.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtils {
    // TODO list of stop-words for sms parsing - never parse an sms if it contains any of them
    //TODO list of go-words for sms parsing - try parse an sms if any of this words is contained

    private static final String PREF_DATABASE_INITIALIZED = "pref_database_initialized";

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
}
