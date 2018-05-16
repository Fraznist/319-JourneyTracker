package com.example.eakgun14.journeytracker.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.eakgun14.journeytracker.DataTypes.LatLngURIPair;
import com.example.eakgun14.journeytracker.R;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

public class PhotoGridAdapter extends BaseAdapter {

    private File parentDirectory;
    private Context mContext;
    private List<LatLngURIPair> pairs;

    public PhotoGridAdapter(Context c, List<LatLngURIPair> paar) {
        mContext = c;
        pairs = paar;
        parentDirectory = c.getExternalFilesDir(Environment.DIRECTORY_DCIM);
    }

    @Override
    public int getCount() {
        return pairs.size();
    }

    @Override
    public Object getItem(int position) {
        return pairs.get(position);
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
        File photoFile = new File(parentDirectory, pairs.get(position).getImageUri());
        loadBitmap(photoFile, imageView);
        int size = (int) mContext.getResources().getDimension(R.dimen.grid_image_size);
        imageView.setLayoutParams(new GridView.LayoutParams(size, size));
        return imageView;
    }

    public void loadBitmap(File image, ImageView view) {
        BitmapWorkerTask task = new BitmapWorkerTask(view);
        task.execute(image);
    }

    private static Bitmap decodeSampledBitmapFromBitmapResource
            (File file, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getPath(), options);
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while (halfHeight / inSampleSize > reqHeight &&
                    halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public List<LatLngURIPair> getPairs() {
        return pairs;
    }

    class BitmapWorkerTask extends AsyncTask<File, Void, Bitmap> {
        private WeakReference<ImageView> imageViewWeakReference;

        public BitmapWorkerTask(ImageView view) {
            imageViewWeakReference = new WeakReference<>(view);
        }

        @Override
        protected Bitmap doInBackground(File... files) {
            File file = files[0];
            return decodeSampledBitmapFromBitmapResource(file,
                    R.dimen.grid_image_size, R.dimen.grid_image_size);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewWeakReference != null && bitmap != null) {
                final  ImageView imageView = imageViewWeakReference.get();
                if (imageView != null)
                    imageView.setImageBitmap(bitmap);
            }
        }
    }
}
