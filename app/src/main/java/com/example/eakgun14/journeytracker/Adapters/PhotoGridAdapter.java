package com.example.eakgun14.journeytracker.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.eakgun14.journeytracker.R;

import java.util.List;

public class PhotoGridAdapter extends BaseAdapter {

    private Context mContext;
    private List<Bitmap> bitmaps;

    public PhotoGridAdapter(Context c, List<Bitmap> images) {
        mContext = c;
        bitmaps = images;
    }

    @Override
    public int getCount() {
        return bitmaps.size();
    }

    @Override
    public Object getItem(int position) {
        return bitmaps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageBitmap(bitmaps.get(position));
        int size = (int) mContext.getResources().getDimension(R.dimen.grid_image_size);
        imageView.setLayoutParams(new GridView.LayoutParams(size, size));
        return imageView;
    }
}
