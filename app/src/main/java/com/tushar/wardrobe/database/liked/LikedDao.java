package com.tushar.wardrobe.database.liked;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface LikedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addToLike(Liked like);

    @Query("SELECT * FROM liked_table")
    LiveData<List<Liked>> getAllLiked();

    @Query("DELETE FROM liked_table WHERE likedID = :likedId")
    void deleteByLikedId(String likedId);
}
