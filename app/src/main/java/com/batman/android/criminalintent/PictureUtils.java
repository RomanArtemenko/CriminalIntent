package com.batman.android.criminalintent;

import android.app.Activity;
//import android.support.v4.app.Ac
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.nfc.Tag;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

/**
 * Created by God on 17.12.2016.
 */

public class PictureUtils {
    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();

        if (Build.VERSION.SDK_INT < 13) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            size.x = display.getWidth();
            size.y = display.getHeight();
        } else {
            activity.getWindowManager().getDefaultDisplay().getSize(size);
        }

        return getScaledBitmap(path, size.x, size.y);
    }

    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        //reading size of image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        //calculate the degree of scaling
        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
/*
            if (srcWidth < srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
*/
            inSampleSize = Math.round(Math.max(srcHeight / destHeight, srcWidth / destWidth));
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        //reading data and creation of the final image
        return BitmapFactory.decodeFile(path, options);
    }
}
