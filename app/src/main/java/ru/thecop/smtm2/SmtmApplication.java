package ru.thecop.smtm2;

import android.app.Application;
import android.util.Log;
import org.greenrobot.greendao.database.Database;
import ru.thecop.smtm2.db.DbHelper;
import ru.thecop.smtm2.model.Category;
import ru.thecop.smtm2.model.DaoMaster;
import ru.thecop.smtm2.model.DaoSession;
import ru.thecop.smtm2.preferences.PreferenceUtils;

public class SmtmApplication extends Application {

    private static final String TAG = "SmtmApplication";
    private static final String DB_NAME = "smtm2";

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        //todo use NON-DEV openhelper in production
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DB_NAME);
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
        if (!PreferenceUtils.isDatabaseInitialized(getApplicationContext())) {
            putInitialData();
            PreferenceUtils.setDatabaseInitialized(getApplicationContext(), true);
        }
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    //todo move to another class?
    private void putInitialData() {
        Log.d(TAG, "Writing initial data");
        String[] initialCategories = getResources().getStringArray(R.array.initial_categories_array);
        for (String initialCategory : initialCategories) {
            Category category = new Category();
            category.setName(initialCategory);
            DbHelper.create(category, this);
        }
    }
}
