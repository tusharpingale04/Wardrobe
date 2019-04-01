package com.tushar.wardrobe.database.liked;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.tushar.wardrobe.database.DatabaseInstance;

import java.util.List;

public class LikedRepository {

    private LikedDao likedDao;

    private LiveData<List<Liked>> likedLiveData;

    public LikedRepository(Application application) {
        DatabaseInstance db = DatabaseInstance.getInstance(application);
        this.likedDao = db.likedDao();
        this.likedLiveData = likedDao.getAllLiked();
    }

    public void addToLike(Liked like) {
        new InsertLikeAsyncTask(likedDao).execute(like);
    }

    public void deleteLike(String likeID) {
        new DeleteLikeAsyncTask(likedDao).execute(likeID);
    }

    public LiveData<List<Liked>> getLikedLiveData() {
        return likedLiveData;
    }

    public static class InsertLikeAsyncTask extends AsyncTask<Liked, Void, Void> {

        private LikedDao likedDao;

        private InsertLikeAsyncTask(LikedDao likedDao) {
            this.likedDao = likedDao;
        }

        @Override
        protected Void doInBackground(Liked... models) {
            likedDao.addToLike(models[0]);
            return null;
        }
    }

    public static class DeleteLikeAsyncTask extends AsyncTask<String, Void, Void> {

        private LikedDao likedDao;

        private DeleteLikeAsyncTask(LikedDao likedDao) {
            this.likedDao = likedDao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            likedDao.deleteByLikedId(strings[0]);
            return null;
        }
    }

}
