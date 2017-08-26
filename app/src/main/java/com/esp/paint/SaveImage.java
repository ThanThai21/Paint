package com.esp.paint;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

public class SaveImage extends AsyncTask<Void, Integer, Boolean> {

    private Context context;
    private Bitmap bitmap;
    private String fileName;
    private ProgressDialog progressDialog;

    public SaveImage(Context context, Bitmap bitmap, String fileName, ProgressDialog progressDialog) {
        this.context = context;
        this.bitmap = bitmap;
        this.fileName = fileName;
        this.progressDialog = progressDialog;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Boolean successful = FileUtils.saveImage(bitmap, fileName);
        return successful;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progressDialog.setMessage("Saving... " + values.toString() + "%");
    }

    @Override
    protected void onPostExecute(Boolean successful) {
        super.onPostExecute(successful);
        if (successful) {
            Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed! Please check storage permission settings", Toast.LENGTH_SHORT).show();
        }
        progressDialog.cancel();
    }
}
