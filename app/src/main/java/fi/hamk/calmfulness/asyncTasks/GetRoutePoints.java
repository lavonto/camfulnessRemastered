package fi.hamk.calmfulness.asyncTasks;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fi.hamk.calmfulness.MapsActivity;
import fi.hamk.calmfulness.helpers.AlertDialogProvider;
import fi.hamk.calmfulness.helpers.GpxHandler;


/**
 * Async task to fetch routes from provided url using {@link OkHttpClient}. After downloading, response is then decoded and returned
 */
public class GetRoutePoints extends AsyncTask<String, Void, Boolean> {

    private AsyncController asyncController;

    // Lists
    private List<LatLng> results;

    // Exceptions
    private Exception exception;

    GetRoutePoints(AsyncController asyncController) {
        this.asyncController = asyncController;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        asyncController.onPreMapsActivityTask(this);
    }

    @Override
    protected Boolean doInBackground(String... routes) {
        if (!isCancelled()) {
            try {
                // Create new OkHttpClient object
                final OkHttpClient client = new OkHttpClient();
                // Set connection timeout to 30sec
                client.setConnectTimeout(30L, TimeUnit.SECONDS); // TODO: What to do on connection timeout ?

                // Create new Request object using Builder and provided url address.
                final Request request = new Request.Builder()
                        .url(routes[0])
                        .build();

                // Store http response to Response object
                final Response response = client.newCall(request).execute();

                // Check if request was success
                if (response.code() == 200) {
                    // Decode gpx file in response
                    results = GpxHandler.decodeGPX(response.body().byteStream());
                } else {
                    throw new Exception(String.valueOf(response.code()));
                }

            } catch (Exception exception) {
                this.exception = exception;
                return false;
            }

            return true;
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean state) {
        super.onPostExecute(state);

        if (exception != null) {
            asyncController.onTaskError("Request Error", exception);
        }

        asyncController.onPostMapsActivityTask(results);
    }

    @Override
    protected void onCancelled(Boolean state) {
        super.onCancelled(state);

        asyncController.onTaskCanceled(GetRoutePoints.class.getName());
    }
}

