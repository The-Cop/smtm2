package ru.thecop.smtm2.db;

import ru.thecop.smtm2.model.DaoSession;

public interface SessionHolder {
    DaoSession getSession();
}
