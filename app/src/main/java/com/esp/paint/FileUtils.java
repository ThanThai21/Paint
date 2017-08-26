package com.esp.paint;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FileUtils {

    public static boolean saveImage(Bitmap bitmap, String fileName) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Paint");
        myDir.mkdirs();
        File file = new File (myDir, fileName + ".png");
        if (file.exists ()) {
            file.delete ();
            file = new File (myDir, fileName);
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
