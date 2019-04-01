package com.tushar.wardrobe.view.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tushar.wardrobe.R;

import java.util.List;

public class MyCustomPagerAdapter extends PagerAdapter{
    private Context context;
    private List<Bitmap> images;
    private LayoutInflater layoutInflater;


    public MyCustomPagerAdapter(Context context, List<Bitmap> images) {
        this.context = context;
        this.images = images;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view,@NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.row_item, container, false);

        ImageView imageView = itemView.findViewById(R.id.imageView);
        imageView.setImageBitmap(images.get(position));
        container.addView(itemView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked :" + (position + 1), Toast.LENGTH_LONG).show();
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position,@NonNull Object object) {
        container.removeView((LinearLayout) object);
    }

    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}