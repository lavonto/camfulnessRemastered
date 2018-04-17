package fi.hamk.calmfulnessV2.asyncTasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.Socket;

/**
 * Async task to download an image from provided url. Downloaded image is returned from task as an bitmap
 *
 */
public class DownloadImage extends AsyncTask<String, Void, Bitmap> {


    private AsyncController asyncController;
    private Exception exception = null;

    // Constructor
    DownloadImage(AsyncController asyncController) {
        this.asyncController = asyncController;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        asyncController.onPreExerciseActivityTask(this);
    }

    @Override
    protected Bitmap doInBackground(String... urls) {

        Bitmap result = null;

        try {
            // Open stream from URL
            InputStream inputStream = new java.net.URL(urls[0]).openStream();

            // decode stream into bitmap
            result = BitmapFactory.decodeStream(inputStream);

            // Close stream when done
            inputStream.close();

        } catch (Exception e) {
            this.exception = e;
        }

        return result;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if (exception != null) {
            asyncController.onTaskError("Stream Error", exception);
        }

        asyncController.onPostExerciseActivityTask(bitmap);
    }

    @Override
    protected void onCancelled(Bitmap bitmap) {
        super.onCancelled(bitmap);

        asyncController.onTaskCanceled(DownloadImage.class.getName());
    }
}
