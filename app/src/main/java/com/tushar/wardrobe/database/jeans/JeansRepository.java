package com.tushar.wardrobe.database.jeans;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.tushar.wardrobe.database.DatabaseInstance;

import java.util.List;

public class JeansRepository {

    private JeansDao jeansDao;

    private LiveData<List<JeansImages>> jeansLiveData;

    public JeansRepository(Application application) {
        DatabaseInstance db = DatabaseInstance.getInstance(application);
        this.jeansDao = db.jeansDao();
        this.jeansLiveData = jeansDao.getAllImages();
    }

    public void insertJeansImages(JeansImages image) {
        new InsertJeansImageAsyncTask(jeansDao).execute(image);
    }

    public LiveData<List<JeansImages>> getJeansLiveData() {
        return jeansLiveData;
    }

    public static class InsertJeansImageAsyncTask extends AsyncTask<JeansImages, Void, Void> {

        private JeansDao jeansDao;

        private InsertJeansImageAsyncTask(JeansDao jeansDao) {
            this.jeansDao = jeansDao;
        }

        @Override
        protected Void doInBackground(JeansImages... models) {
            jeansDao.insertImage(models[0]);
            return null;
        }
    }

}
