package fi.hamk.calmfulnessV2.asyncTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;

import fi.hamk.calmfulnessV2.MainActivity;
import fi.hamk.calmfulnessV2.azure.AzureServiceAdapter;
import fi.hamk.calmfulnessV2.azure.AzureTableHandler;
import fi.hamk.calmfulnessV2.helpers.AlertDialogProvider;

public class InitAzure extends AsyncTask<Void, Void, Boolean> {

    // Log tag
    private String TAG = InitAzure.class.getName();

    // Objects
    private WeakReference<Context> weakContext;
    private WeakReference<Activity> weakActivity;
    private Exception e;

    // Constructor
    InitAzure(WeakReference<Context> weakContext, WeakReference<Activity> weakActivity) {
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


        Log.d(TAG, "Initializing Azure...");
        ((MainActivity)weakActivity.get()).showProgressbar(true);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            //Initialize Adapter. Weak context reference is not acceptable, so use get() method to send context reference
                AzureServiceAdapter.Initialize(weakContext.get());

            //Initialize TableHandler with AzureServiceAdapter instance
            AzureTableHandler.Initialize(AzureServiceAdapter.getInstance());

            if (!AzureServiceAdapter.checkLocalStorage()) {
                //Initialize local storage
                AzureTableHandler.initLocalStorage();
                //Populate the created tables
                AzureTableHandler.refreshTables();
            }

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

        Log.d(TAG, "Azure initialization done. Result: " + state);
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
