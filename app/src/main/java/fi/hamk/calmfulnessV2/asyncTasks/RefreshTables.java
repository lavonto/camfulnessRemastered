package fi.hamk.calmfulnessV2.asyncTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

import fi.hamk.calmfulnessV2.MainActivity;
import fi.hamk.calmfulnessV2.azure.AzureTableHandler;
import fi.hamk.calmfulnessV2.helpers.AlertDialogProvider;


public class RefreshTables extends AsyncTask<Void, Void, Boolean> {

    // Log tag
    private String TAG = RefreshTables.class.getName();

    // Objects
    private WeakReference<Context> weakContext;
    private WeakReference<Activity> weakActivity;
    private Exception e;

    // Constructor
    RefreshTables(Context context, Activity activity) {
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
            e = new NullPointerException("Context, activity or both were null");
        }

        Log.d(TAG, "Attempting to refresh tables...");
        ((MainActivity)weakActivity.get()).setProgressbarState(true);

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (!isCancelled()) {
            try {
                AzureTableHandler.refreshTables();
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
        if (e != null) {
            new AlertDialogProvider(weakContext.get()).createAndShowDialog("RefreshTables Error", e.toString());
        }

        Log.d(TAG, "RefreshTables Done! Result: " + state);

        // Weak context reference is not acceptable, so - again - use get() method to send context reference
        if (weakContext.get() != null || weakActivity.get() != null) {
            ((MainActivity) weakActivity.get()).setMenuButtonState(state);
            ((MainActivity) weakActivity.get()).setProgressbarState(false);
        }

    }

    @Override
    protected void onCancelled(Boolean state) {
        super.onCancelled(state);

        if (weakContext.get() != null || weakActivity.get() != null) {
            new AlertDialogProvider(weakContext.get()).createAndShowDialog("RefreshTables Error", e.toString());
            ((MainActivity) weakActivity.get()).setMenuButtonState(false);
            ((MainActivity) weakActivity.get()).setProgressbarState(false);
        }
    }
}
