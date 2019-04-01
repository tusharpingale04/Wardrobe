package com.tushar.wardrobe.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;

import com.tushar.wardrobe.database.jeans.JeansImages;
import com.tushar.wardrobe.database.jeans.JeansRepository;
import com.tushar.wardrobe.database.liked.Liked;
import com.tushar.wardrobe.database.liked.LikedRepository;
import com.tushar.wardrobe.database.shirts.ShirtImages;
import com.tushar.wardrobe.database.shirts.ShirtsRepository;

import java.util.List;

public class ImagesViewModel extends AndroidViewModel {

    private LiveData<List<ShirtImages>> shirtsLiveData;
    private ShirtsRepository shirtsRepository;

    private LiveData<List<JeansImages>> jeansLiveData;
    private JeansRepository jeansRepository;

    private LiveData<List<Liked>> likedLiveData;
    private LikedRepository likedRepository;

    public ObservableInt fabVisibility = new ObservableInt(8);

    public ImagesViewModel(@NonNull Application application) {
        super(application);
        shirtsRepository = new ShirtsRepository(application);
        shirtsLiveData = shirtsRepository.getShirtsLiveData();

        jeansRepository = new JeansRepository(application);
        jeansLiveData = jeansRepository.getJeansLiveData();

        likedRepository = new LikedRepository(application);
        likedLiveData = likedRepository.getLikedLiveData();
    }

    public void insertShirtImage(ShirtImages images) {
        shirtsRepository.insertShirtImages(images);
    }

    public LiveData<List<ShirtImages>> getShirtsImagesLiveData() {
        return shirtsLiveData;
    }

    public void insertJeansImage(JeansImages images) {
        jeansRepository.insertJeansImages(images);
    }

    public LiveData<List<JeansImages>> getJeansLiveData() {
        return jeansLiveData;
    }

    public void addToLike(Liked like) {
        likedRepository.addToLike(like);
    }

    public void deleteLike(String likeID) {
        likedRepository.deleteLike(likeID);
    }

    public LiveData<List<Liked>> getLikedLiveData() {
        return likedLiveData;
    }

}
