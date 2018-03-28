package fi.hamk.calmfulnessV2.asyncTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

import fi.hamk.calmfulnessV2.MainActivity;
import fi.hamk.calmfulnessV2.MapsActivity;
import fi.hamk.calmfulnessV2.azure.RouteContainer;
import fi.hamk.calmfulnessV2.helpers.AlertDialogProvider;


public class InitRouteContainer extends AsyncTask<Void, Void, Boolean> {

    // Log tag
    private String TAG = InitLocalStorage.class.getName();

    // Objects
    private WeakReference<Context> weakContext;
    private WeakReference<Activity> weakActivity;
    private Exception e;

    // Constructor
    InitRouteContainer(Context context, Activity activity) {
        this.weakContext = new WeakReference<>(context);
        this.weakActivity = new WeakReference<>(activity);
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
        Log.d(TAG, "Initializing route container...");
        ((MapsActivity)weakActivity.get()).setProgressbarState(true);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (!isCancelled()) {
            //List for LatLng points
            try {
                //Initialize Adapter
                RouteContainer.Initialize();
                Log.i(TAG, "Storage initialized");

            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
                // Store value of caught error for later use
                this.e = e;
                return false;
            }
            return true;
        }
        return  false;
    }

    @Override
    protected void onPostExecute(Boolean state) {
        super.onPostExecute(state);

        // If there's caught exception in e, then create a new dialog and show it
        if (e != null && weakContext.get() != null) {
            new AlertDialogProvider(weakContext.get()).createAndShowExceptionDialog("InitRouteContainer Error", e);
        }

        Log.d(TAG, "InitRouteContainer Done! Result: " + state);

        if (weakActivity.get() != null) {
            ((MapsActivity) weakActivity.get()).setProgressbarState(false);
        }
    }

    @Override
    protected void onCancelled(Boolean state) {
        super.onCancelled(state);

        if (weakContext.get() != null || weakActivity.get() != null) {
            new AlertDialogProvider(weakContext.get()).createAndShowExceptionDialog("InitRouteContainer Error", e);
            ((MapsActivity) weakActivity.get()).setProgressbarState(false);
        }
    }
}
