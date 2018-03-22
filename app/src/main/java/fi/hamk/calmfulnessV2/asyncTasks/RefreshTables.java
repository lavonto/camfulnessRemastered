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

        if (weakContext == null || weakActivity == null) {
            this.cancel(true);
            e = new Exception("Task canceled. Reference to context or activity or both were null. CONTEXT: " + weakContext + " ACTIVITY: " + weakActivity);
        }

        Log.d(TAG, "Attempting to refresh tables...");
        ((MainActivity)weakActivity.get()).showProgressbar(true);

    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            AzureTableHandler.refreshTables();
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
            this.e = e;
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean state) {
        super.onPostExecute(state);

        if (e != null) {
            new AlertDialogProvider(weakContext.get()).createAndShowExceptionDialog("title", e);
        }

        Log.d(TAG, "Refreshing tables done. Result: " + state);
        ((MainActivity)weakActivity.get()).azureSuccess(state);
        ((MainActivity)weakActivity.get()).showProgressbar(false);

    }

    @Override
    protected void onCancelled(Boolean state) {
        super.onCancelled(state);

        new AlertDialogProvider(weakContext.get()).createAndShowExceptionDialog("title", e);
        ((MainActivity)weakActivity.get()).azureSuccess(false);
        ((MainActivity)weakActivity.get()).showProgressbar(false);
    }
}
