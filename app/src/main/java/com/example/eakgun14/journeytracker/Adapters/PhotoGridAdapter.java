package com.example.eakgun14.journeytracker.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.eakgun14.journeytracker.DataTypes.LatLngNamePair;
import com.example.eakgun14.journeytracker.R;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

public class PhotoGridAdapter extends BaseAdapter {

    private Context mContext;
    private DynamicViewManager<LatLngNamePair> viewManager;
    private ViewAdapterListener<LatLngNamePair> listener;

    public PhotoGridAdapter(Context c, ViewAdapterListener<LatLngNamePair> listen,
                            List<LatLngNamePair> pairs) {
        mContext = c;
        listener = listen;
        viewManager = new DynamicViewManager<>(pairs);
    }

    @Override
    public int getCount() {
        return viewManager.size();
    }

    @Override
    public Object getItem(int position) {
        return viewManager.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        File parentDirectory = mContext.getExternalFilesDir(Environment.DIRECTORY_DCIM);

        final LatLngNamePair pair = viewManager.getCurrent().get(position);
        Log.d("pic", viewManager.getCurrent().toString());

        CheckableImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new CheckableImageView(mContext);
            int size = (int) mContext.getResources().getDimension(R.dimen.grid_image_size);
            imageView.setLayoutParams(new GridView.LayoutParams(size, size));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (CheckableImageView) convertView;
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onViewItemClicked(pair);
            }
        });
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (viewManager.getSelected().contains(pair))
                    viewManager.removeFromSelected(pair);
                else
                    viewManager.addAsSelected(pair);
                ((CheckableImageView) v).toggle();
                Log.d("long", "longClicked! isSelected: " + ((CheckableImageView) v).isChecked());
                return true;
            }
        });
        File photoFile = new File(parentDirectory, viewManager.get(position).getName());
        loadBitmap(photoFile, imageView);
        return imageView;
    }

    public void removeSelected() {
        for (LatLngNamePair pair : viewManager.getSelected())
            remove(pair);
        viewManager.clearSelected();
        notifyDataSetChanged();
    }

    private void remove(LatLngNamePair pair) {
        viewManager.remove(pair);
    }

    private void loadBitmap(File image, ImageView view) {
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

    @SuppressLint("StaticFieldLeak")
    class BitmapWorkerTask extends AsyncTask<File, Void, Bitmap> {
        private WeakReference<ImageView> imageViewWeakReference;

        BitmapWorkerTask(ImageView view) {
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

    class CheckableImageView extends android.support.v7.widget.AppCompatImageView
            implements Checkable {

        private boolean mChecked = false;
        private final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

        public CheckableImageView(Context ctx) {
            super(ctx);
        }

        public CheckableImageView(Context ctx, AttributeSet attrs) {
            super(ctx, attrs);
        }

        @Override
        public int[] onCreateDrawableState(final int extraSpace) {
            final int[] drawableState = super.onCreateDrawableState(extraSpace);
            if (isChecked())
                mergeDrawableStates(drawableState, CHECKED_STATE_SET);
            return drawableState;
        }

        @Override
        public void toggle() {
            setChecked(!mChecked);
        }

        @Override
        public void setChecked(boolean checked) {
            if (mChecked != checked) {
                mChecked = checked;
                if (isChecked())
                    this.setColorFilter(Color.argb(150,255,255,255));
                else
                    this.setColorFilter(Color.argb(0,255,255,255));
                refreshDrawableState();
            }
        }

        @Override
        public boolean isChecked() {
            return mChecked;
        }
    }
}
