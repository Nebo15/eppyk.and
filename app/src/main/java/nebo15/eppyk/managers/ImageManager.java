package nebo15.eppyk.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.Date;


/**
 * Created by anton on 17/04/16.
 */
public class ImageManager {



    public static Bitmap getScreenShot(View view, Context context) {

        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache(true));
        screenView.setDrawingCacheEnabled(false);

        int statusHeight = getStatusBarHeight(context);
        bitmap = Bitmap.createBitmap(bitmap, 0, statusHeight, bitmap.getWidth(), bitmap.getHeight() - statusHeight);

        return bitmap;
    }

    static private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    static public void saveImage(Bitmap image) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());


            //You can create a new file name "test.jpg" in sdcard folder.
            File f = new File(String.format("%s/EPPYK/EPPYK %s.jpg", Environment.getExternalStorageDirectory(), currentDateTimeString) );

            f.getParentFile().mkdirs();

            f.createNewFile();
            //Write the bytes in file
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());

            //Remember close the FileOutput
            fo.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }


}
