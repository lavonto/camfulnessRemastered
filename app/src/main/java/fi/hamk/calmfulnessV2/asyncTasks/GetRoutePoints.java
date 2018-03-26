package fi.hamk.calmfulnessV2.asyncTasks;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

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
    private Exception e;

    private List<LatLng> latLngs = new ArrayList<>();

    // Constructor
    GetRoutePoints(WeakReference<Context> weakContext, WeakReference<Activity> weakActivity) {
        this.weakContext = weakContext;
        this.weakActivity = weakActivity;
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
            try {
                //Go through all uploaded routes
                for (String blobName : RouteContainer.getBlobNames()) {
                    try {
                        //Get BlobInputStream from RouteContainer
                        final InputStream stream = RouteContainer.getInputStream(blobName);
                        //Get the LatLng points from the Blob
                        final List<LatLng> result = GpxHandler.decodeGPX(stream);

                        // Add the results
                        latLngs.addAll(result);
                        // Set latLngs in MapsActivity for later use
                        MapsActivity.setLatLngs(latLngs);

                    } catch (Exception e) {
                        Log.e(TAG, e.toString(), e);
                        // Store value of caught error for later use
                        this.e = e;
                        return false;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
                // Store value of caught error for later use
                this.e = e;
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean state) {
        super.onPostExecute(state);

        // If there's caught exception in e, then create a new dialog and show it
        if (weakActivity.get() != null && weakContext.get() != null) {
            if (e != null) {
                new AlertDialogProvider(weakContext.get()).createAndShowExceptionDialog("GetRoutePoints Error", e);
            }

            Log.d(TAG, "Fetching blobs Done. Result: " + state);
            ((MapsActivity) weakActivity.get()).setProgressbarState(false);

            if (latLngs.isEmpty()) {
                Log.e(TAG, "A selected route is empty");
                new AlertDialogProvider(weakContext.get()).createAndShowExceptionDialog("Route Error", new Exception("A selected route is empty"));
            } else {
                ((MapsActivity) weakActivity.get()).drawRouteOnMap(latLngs);
            }
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

