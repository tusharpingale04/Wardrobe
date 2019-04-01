package com.tushar.wardrobe.database.shirts;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.tushar.wardrobe.database.DatabaseInstance;

import java.util.List;

public class ShirtsRepository {

    private ShirtsDao shirtsDao;

    private LiveData<List<ShirtImages>> shirtsLiveData;

    public ShirtsRepository(Application application) {
        DatabaseInstance db = DatabaseInstance.getInstance(application);
        this.shirtsDao = db.shirtsDao();
        this.shirtsLiveData = shirtsDao.getAllImages();
    }

    public void insertShirtImages(ShirtImages image) {
        new InsertShirtImageAsyncTask(shirtsDao).execute(image);
    }

    public LiveData<List<ShirtImages>> getShirtsLiveData(){
        return shirtsLiveData;
    }

    public static class InsertShirtImageAsyncTask extends AsyncTask<ShirtImages, Void, Void> {

        private ShirtsDao shirtsDao;

        private InsertShirtImageAsyncTask(ShirtsDao shirtsDao) {
            this.shirtsDao = shirtsDao;
        }

        @Override
        protected Void doInBackground(ShirtImages... models) {
            shirtsDao.insertImage(models[0]);
            return null;
        }
    }

}
