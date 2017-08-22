package ru.thecop.smtm2.model;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class Spending {
    @Id
    private Long id;

    //TODO test updated on create-update-delete
    @NotNull
    private long updatedTimestamp;

    @NotNull
    @Index
    private long timestamp;

    @NotNull
    private double amount;

    @NotNull
    @ToOne(joinProperty = "categoryId")
    private Category category;
    private long categoryId;

    private String comment;

    private String smsText;

    private String smsFrom;

    @NotNull
    private boolean confirmed;//For sms auto parsing - user must confirm sms parsed correctly

    @NotNull
    private boolean deleted;

    @Unique
    @NotNull
    private String uid;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 593183288)
    private transient SpendingDao myDao;

    @Generated(hash = 1398711129)
    public Spending(Long id, long updatedTimestamp, long timestamp, double amount,
                    long categoryId, String comment, String smsText, String smsFrom,
                    boolean confirmed, boolean deleted, @NotNull String uid) {
        this.id = id;
        this.updatedTimestamp = updatedTimestamp;
        this.timestamp = timestamp;
        this.amount = amount;
        this.categoryId = categoryId;
        this.comment = comment;
        this.smsText = smsText;
        this.smsFrom = smsFrom;
        this.confirmed = confirmed;
        this.deleted = deleted;
        this.uid = uid;
    }

    @Generated(hash = 2056300050)
    public Spending() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    public void setUpdatedTimestamp(long updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSmsText() {
        return this.smsText;
    }

    public void setSmsText(String smsText) {
        this.smsText = smsText;
    }

    public String getSmsFrom() {
        return this.smsFrom;
    }

    public void setSmsFrom(String smsFrom) {
        this.smsFrom = smsFrom;
    }

    public boolean getConfirmed() {
        return this.confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public boolean getDeleted() {
        return this.deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Generated(hash = 1372501278)
    private transient Long category__resolvedKey;

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 234631651)
    public Category getCategory() {
        long __key = this.categoryId;
        if (category__resolvedKey == null || !category__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CategoryDao targetDao = daoSession.getCategoryDao();
            Category categoryNew = targetDao.load(__key);
            synchronized (this) {
                category = categoryNew;
                category__resolvedKey = __key;
            }
        }
        return category;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1927364589)
    public void setCategory(@NotNull Category category) {
        if (category == null) {
            throw new DaoException(
                    "To-one property 'categoryId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.category = category;
            categoryId = category.getId();
            category__resolvedKey = categoryId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    @Override
    public String toString() {
        return "Spending{" +
                "id=" + id +
                ", updatedTimestamp=" + updatedTimestamp +
                ", timestamp=" + timestamp +
                ", amount=" + amount +
                ", categoryId=" + categoryId +
                ", comment='" + comment + '\'' +
                ", smsText='" + smsText + '\'' +
                ", smsFrom='" + smsFrom + '\'' +
                ", confirmed=" + confirmed +
                ", deleted=" + deleted +
                ", uid='" + uid + '\'' +
                '}';
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 471661326)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getSpendingDao() : null;
    }
}
