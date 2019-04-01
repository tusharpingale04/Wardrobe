package com.tushar.wardrobe.database.liked;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "liked_table")
public class Liked {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String likedID;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLikedID() {
        return likedID;
    }

    public void setLikedID(String likedID) {
        this.likedID = likedID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Liked) {
            Liked tmpObj = (Liked) obj;
            return tmpObj.likedID != null && tmpObj.likedID.equals(this.likedID);
        }
        return false;
    }
}
