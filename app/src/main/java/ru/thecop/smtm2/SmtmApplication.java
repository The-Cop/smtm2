package ru.thecop.smtm2;

import android.app.Application;
import android.content.Context;
import org.greenrobot.greendao.database.Database;
import ru.thecop.smtm2.db.ContextAndSessionHolder;
import ru.thecop.smtm2.model.DaoMaster;
import ru.thecop.smtm2.model.DaoSession;
import ru.thecop.smtm2.util.InitializationHelper;

//todo ripple animation on buttons and selectable list items
public class SmtmApplication extends Application implements ContextAndSessionHolder {

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
        InitializationHelper.initializeAppData(this);
    }

    @Override
    public DaoSession getSession() {
        return daoSession;
    }

    @Override
    public Context getContext() {
        return this;
    }
}
