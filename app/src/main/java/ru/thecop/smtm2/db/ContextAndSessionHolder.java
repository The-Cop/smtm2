package ru.thecop.smtm2.db;

import android.content.Context;
import ru.thecop.smtm2.model.DaoSession;

public interface ContextAndSessionHolder {
    DaoSession getSession();

    Context getContext();
}
