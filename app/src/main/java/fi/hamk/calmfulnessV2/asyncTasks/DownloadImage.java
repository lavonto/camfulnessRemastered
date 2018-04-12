package fi.hamk.calmfulnessV2.asyncTasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.Socket;

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

    private static String TAG = DownloadImage.class.getName();

    private AsyncController asyncController;

    // Exceptions
    private Exception exception = null;

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
            Log.d(TAG, "Getting input stream from: " + urls[0]);

            // decode stream into bitmap
            result = BitmapFactory.decodeStream(inputStream);
            if (result != null) {
                Log.d(TAG, "I have a present for ya!");
            }

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

        asyncController.onPostExerciseActivityTask(true, bitmap);
    }

    @Override
    protected void onCancelled(Bitmap bitmap) {
        super.onCancelled(bitmap);

        asyncController.onTaskCanceled(DownloadImage.class.getName());
    }
}
