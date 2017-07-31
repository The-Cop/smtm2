package ru.thecop.smtm2.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class Category {
    @Id
    private Long id;

    //TODO test updated on create-update-delete
    @NotNull
    private long updatedTimestamp;

    @NotNull
    @Unique
    private String name;

    @NotNull
    @Unique
    private String lowerCaseName;

    @Generated(hash = 1584941244)
    public Category(Long id, long updatedTimestamp, @NotNull String name,
            @NotNull String lowerCaseName) {
        this.id = id;
        this.updatedTimestamp = updatedTimestamp;
        this.name = name;
        this.lowerCaseName = lowerCaseName;
    }

    @Generated(hash = 1150634039)
    public Category() {
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLowerCaseName() {
        return this.lowerCaseName;
    }

    public void setLowerCaseName(String lowerCaseName) {
        this.lowerCaseName = lowerCaseName;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", updatedTimestamp=" + updatedTimestamp +
                ", name='" + name + '\'' +
                ", lowerCaseName='" + lowerCaseName + '\'' +
                '}';
    }
}
