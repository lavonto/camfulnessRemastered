package fi.hamk.calmfulnessV2.asyncTasks;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.collect.Maps;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import fi.hamk.calmfulnessV2.MapsActivity;
import fi.hamk.calmfulnessV2.azure.RouteContainer;
import fi.hamk.calmfulnessV2.helpers.AlertDialogProvider;
import fi.hamk.calmfulnessV2.helpers.GpxHandler;


public class GetRoutePoints extends AsyncTask<Void, Void, Boolean> {

    // Log tag
    private String TAG = GetRoutePoints.class.getName();

    // Objects
    private WeakReference<Context> weakContext;
    private WeakReference<Activity> weakActivity;
    private  Response response;

    private Exception e;

    private List<String> routes;
    private List<List<LatLng>> results = new ArrayList<>();

    // Constructor
    GetRoutePoints(Context context, Activity activity) {
        this.weakContext = new WeakReference<>(context);
        this.weakActivity = new WeakReference<>(activity);
    }

    // Constructor
    GetRoutePoints(Context context, Activity activity, List<String> routes) {
        this.weakContext = new WeakReference<>(context);
        this.weakActivity = new WeakReference<>(activity);
        this.routes = routes;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // Check if context and activity references are not null - activity has been destroyed
        if (weakContext.get() == null || weakActivity.get() == null) {
            // Canceling task will result in onCancelled(Object) being invoked on the UI thread.
            // Note! Canceling task guarantees that onPostExecute(Object) is never invoked.
            this.cancel(true);
            e = new Exception("Task canceled. Reference to context or activity or both were null. CONTEXT: " + weakContext + " ACTIVITY: " + weakActivity);
        }

        Log.d(TAG, "Fetching route points...");
        ((MapsActivity) weakActivity.get()).setProgressbarState(true);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (!isCancelled()) {
            for (String url : routes) {
                try {
                   final  OkHttpClient client = new OkHttpClient();
                    final Request request = new Request.Builder()
                            .url(url)
                            .build();

                    response = client.newCall(request).execute();
                    Log.d(TAG, "RESPONSE: " + response);

                    if (response.code() == 200) {
                        results.add(GpxHandler.decodeGPX(response.body().byteStream()));
                    } else {
                        Log.d(TAG, "Something went wrong: " + response.code());
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.toString(), e);
                    // Store value of caught error for later use
                    this.e = e;
                    return false;
                }
            }
                return true;
            }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean state) {
        super.onPostExecute(state);

        // If there's caught exception in e, then create a new dialog and show it
        if (weakActivity.get() != null) {

            for (List<LatLng> latLngs : results) {
                ((MapsActivity)weakActivity.get()).drawRouteOnMap(latLngs);
            }

            if (e != null && weakContext.get() != null) {
                new AlertDialogProvider(weakContext.get()).createAndShowExceptionDialog("GetRoutePoints Error", e);
            }

            Log.d(TAG, "Fetching blobs Done. Result: " + state);
            ((MapsActivity) weakActivity.get()).setProgressbarState(false);
        }
    }

    @Override
    protected void onCancelled(Boolean state) {
        super.onCancelled(state);

        if (weakContext.get() != null || weakActivity.get() != null) {
            new AlertDialogProvider(weakContext.get()).createAndShowExceptionDialog("GetRoutePoints Error", e);
            ((MapsActivity) weakActivity.get()).setProgressbarState(false);
        }
    }
}

