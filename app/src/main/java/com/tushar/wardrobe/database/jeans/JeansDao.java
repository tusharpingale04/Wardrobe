package com.tushar.wardrobe.database.jeans;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface JeansDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertImage(JeansImages image);

    @Query("SELECT * FROM jeans_images_table")
    LiveData<List<JeansImages>> getAllImages();
}
