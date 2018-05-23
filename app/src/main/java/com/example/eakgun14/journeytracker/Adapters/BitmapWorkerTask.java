package com.example.eakgun14.journeytracker.Adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.eakgun14.journeytracker.R;

import java.io.File;
import java.lang.ref.WeakReference;

@SuppressLint("StaticFieldLeak")
class BitmapWorkerTask extends AsyncTask<File, Void, Bitmap> {
    private WeakReference<ImageView> imageViewWeakReference;
//    private WeakReference<Integer> widthWeakReference;
//    private WeakReference<Integer> heightWeakReference;

    BitmapWorkerTask(ImageView view, int w, int h) {
        imageViewWeakReference = new WeakReference<>(view);
//        widthWeakReference = new WeakReference<>(w);
//        heightWeakReference = new WeakReference<>(h);
    }

    @Override
    protected Bitmap doInBackground(File... files) {
        File file = files[0];
        int w = R.dimen.grid_image_size;
        int h = w;
//        int w = widthWeakReference.get();
//        int h = heightWeakReference.get();
        return decodeSampledBitmapFromBitmapResource(file, w, h);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewWeakReference != null && bitmap != null) {
            final  ImageView imageView = imageViewWeakReference.get();
            if (imageView != null)
                imageView.setImageBitmap(bitmap);
        }
    }

    static void loadBitmap(File image, ImageView view) {
        int size = R.dimen.grid_image_size;
        Log.d("img", image.toString());
        BitmapWorkerTask task = new BitmapWorkerTask(view, size, size);
        task.execute(image);
    }

    static void loadBitmapSync(File image, ImageView view) {
        int size = R.dimen.grid_image_size;
        Log.d("img", image.toString());
        Bitmap bit = decodeSampledBitmapFromBitmapResource(image, size, size);
        view.setImageBitmap(bit);
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
}
