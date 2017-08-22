package ru.thecop.smtm2.db;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import org.greenrobot.greendao.database.Database;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import ru.thecop.smtm2.model.DaoMaster;
import ru.thecop.smtm2.model.DaoSession;

@RunWith(AndroidJUnit4.class)
public class GenericDatabaseTest implements SessionHolder {

    private static final String GENERIC_TAG = "GenericDatabaseTest";
    private static final String DB_NAME = "test_smtm2";
    private DaoSession daoSession;

    @Before
    public void before() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(appContext, DB_NAME);
        Database db = helper.getWritableDb();
        DaoMaster.createAllTables(db, true);
        daoSession = new DaoMaster(db).newSession();
        Log.d(GENERIC_TAG, "Created database");
    }

    @After
    public void after() {
        DaoMaster.dropAllTables(daoSession.getDatabase(), true);
        Log.d(GENERIC_TAG, "Dropped tables");
    }

    @Override
    public DaoSession getSession() {
        return daoSession;
    }

    protected void clearCache() {
        daoSession.clear();
    }
}
