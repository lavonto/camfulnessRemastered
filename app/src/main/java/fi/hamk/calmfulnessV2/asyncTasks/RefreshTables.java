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
    RefreshTables(WeakReference<Context> weakContext, WeakReference<Activity> weakActivity) {
        this.weakContext = weakContext;
        this.weakActivity = weakActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // Check if context and activity references are not null - activity has been destroyed
        if (weakContext == null || weakActivity == null) {
            // Canceling task will result in onCancelled(Object) being invoked on the UI thread.
            // Note! Canceling task guarantees that onPostExecute(Object) is never invoked.
            this.cancel(true);
            e = new Exception("Task canceled. Reference to context or activity or both were null. CONTEXT: " + weakContext + " ACTIVITY: " + weakActivity);
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
            new AlertDialogProvider(weakContext.get()).createAndShowExceptionDialog("RefreshTables error", e);
        }

        Log.d(TAG, "Refreshing tables done. Result: " + state);
        ((MainActivity)weakActivity.get()).azureSuccess(state);
        ((MainActivity)weakActivity.get()).setProgressbarState(false);

    }

    @Override
    protected void onCancelled(Boolean state) {
        super.onCancelled(state);

        new AlertDialogProvider(weakContext.get()).createAndShowExceptionDialog("title", e);
        ((MainActivity)weakActivity.get()).azureSuccess(false);
        ((MainActivity)weakActivity.get()).setProgressbarState(false);
    }
}
