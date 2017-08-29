package ru.thecop.smtm2.util;

import android.content.Context;
import android.util.Log;
import ru.thecop.smtm2.R;
import ru.thecop.smtm2.db.ContextAndSessionHolder;
import ru.thecop.smtm2.db.DbDevUtils;
import ru.thecop.smtm2.db.DbHelper;
import ru.thecop.smtm2.model.Category;
import ru.thecop.smtm2.preferences.PreferenceUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//todo test this
public class InitializationHelper {

    private static final String TAG = "InitializationHelper";

    public static void initializeAppData(ContextAndSessionHolder contextAndSessionHolder) {
        if (!PreferenceUtils.isDatabaseInitialized(contextAndSessionHolder.getContext())) {
            init(contextAndSessionHolder);
            PreferenceUtils.setDatabaseInitialized(contextAndSessionHolder.getContext(), true);
        }
    }

    private static void init(ContextAndSessionHolder contextAndSessionHolder) {
        Log.d(TAG, "Writing initial data");
        putInitialCategories(contextAndSessionHolder);
        putInitialSmsParseStopWords(contextAndSessionHolder.getContext());
        putInitialSmsParseGoWords(contextAndSessionHolder.getContext());

        DbDevUtils.fillDataBase(contextAndSessionHolder);//TODO delete dev database filling
    }

    private static void putInitialSmsParseGoWords(Context context) {
        String[] initialSmsParseGoWords = context.getResources().getStringArray(R.array.initial_sms_parse_go_words);
        Set<String> set = new HashSet<>(Arrays.asList(initialSmsParseGoWords));
        PreferenceUtils.setSmsParseGoWords(context, set);
    }

    private static void putInitialSmsParseStopWords(Context context) {
        String[] initialSmsParseStopWords = context.getResources().getStringArray(R.array.initial_sms_parse_stop_words);
        Set<String> set = new HashSet<>(Arrays.asList(initialSmsParseStopWords));
        PreferenceUtils.setSmsParseStopWords(context, set);
    }

    private static void putInitialCategories(ContextAndSessionHolder contextAndSessionHolder) {
        String[] initialCategories = contextAndSessionHolder.getContext().getResources().getStringArray(R.array.initial_categories_array);
        List<Category> categories = new ArrayList<>(initialCategories.length);
        for (String initialCategory : initialCategories) {
            Category category = new Category();
            category.setName(initialCategory);
            categories.add(category);
        }
        DbHelper.createCategoriesInSingleTransaction(categories, contextAndSessionHolder);
    }
}
