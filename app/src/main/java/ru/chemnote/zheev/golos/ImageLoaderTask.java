package ru.chemnote.zheev.golos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by zheev on 13.03.18.
 */

public class ImageLoaderTask extends AsyncTask<Object, Void, Bitmap> {

    private volatile LevelListDrawable mDrawable;

    private String source;
    private String result;
    private TextView tv;
    private final String TAG = "WS";

    public ImageLoaderTask(String s, String result, TextView tv) {
        this.source = s;
        this.result = result;
        this.tv = tv;
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        try{
            InputStream is = new URL(source).openStream();

            mDrawable = (LevelListDrawable) params[0];

            return BitmapFactory.decodeStream(is);

        }catch(MalformedURLException e){

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onPostExecute(Bitmap bitmap)
    {
        Log.d(TAG, "onPostExecute bitmap " + bitmap);
        if (bitmap != null) {
            BitmapDrawable d = new BitmapDrawable(bitmap);
            mDrawable.addLevel(1, 1, d);
            mDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
            mDrawable.setLevel(1);
            // i don't know yet a better way to refresh TextView
            // mTv.invalidate() doesn't work as expected
            CharSequence t = tv.getText();
            tv.setText(t);
        }
    }


}
