package com.tushar.wardrobe.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.tushar.wardrobe.database.jeans.JeansDao;
import com.tushar.wardrobe.database.jeans.JeansImages;
import com.tushar.wardrobe.database.liked.Liked;
import com.tushar.wardrobe.database.liked.LikedDao;
import com.tushar.wardrobe.database.shirts.ShirtImages;
import com.tushar.wardrobe.database.shirts.ShirtsDao;

@Database(entities = {ShirtImages.class, JeansImages.class, Liked.class}, version = 1)
public abstract class DatabaseInstance extends RoomDatabase {

    private static DatabaseInstance instance;

    public abstract ShirtsDao shirtsDao();

    public abstract JeansDao jeansDao();

    public abstract LikedDao likedDao();

    public static synchronized DatabaseInstance getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), DatabaseInstance.class, "wardrobe_db")
                    .fallbackToDestructiveMigration()
                    .addCallback(callback)
                    .build();
        }
        return instance;
    }

    private static Callback callback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };
}
