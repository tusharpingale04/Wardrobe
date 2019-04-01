package com.tushar.wardrobe.view.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tushar.wardrobe.R;
import com.tushar.wardrobe.database.jeans.JeansImages;
import com.tushar.wardrobe.interfaces.IJeansAdded;
import com.tushar.wardrobe.interfaces.IJeansPosition;
import com.tushar.wardrobe.interfaces.IJeansShuffle;
import com.tushar.wardrobe.utilities.BitmapUtils;
import com.tushar.wardrobe.utilities.Utility;
import com.tushar.wardrobe.view.adapters.MyCustomPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class JeansFragment extends Fragment implements IJeansShuffle, IJeansAdded {

    private List<JeansImages> imagesList;
    private ViewPager viewPager;
    private MyCustomPagerAdapter myCustomPagerAdapter;
    private Context mContext;
    private List<Bitmap> bitmapList;
    private IJeansPosition listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shirt_jeans, container, false);
        viewPager = view.findViewById(R.id.viewPager);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpViewPager();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                listener.onJeansChanged(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public int onJeansShuffled() {
        int position = Utility.getRandomNumberFrom(0,myCustomPagerAdapter.getCount() - 1);
        myCustomPagerAdapter.notifyDataSetChanged();
        viewPager.setAdapter(null);
        viewPager.setAdapter(myCustomPagerAdapter);
        viewPager.setCurrentItem(position);
        return viewPager.getCurrentItem();
    }

    @Override
    public void jeansAdded(List<JeansImages> imagesList) {
        this.imagesList = imagesList;
        setUpViewPager();
    }

    private void setUpViewPager() {
        if (imagesList != null && imagesList.size() > 0) {
            new ViewPagerSetup().execute();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        listener = (IJeansPosition) context;
    }

    private class ViewPagerSetup extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            bitmapList = new ArrayList<>();
            for (int i = 0; i < imagesList.size(); i++) {
                bitmapList.add(BitmapUtils.convertCompressedByteArrayToBitmap(imagesList.get(i).getImage()));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            myCustomPagerAdapter = new MyCustomPagerAdapter(mContext, bitmapList);
            viewPager.setAdapter(myCustomPagerAdapter);
        }
    }
}
