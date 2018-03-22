package fi.hamk.calmfulnessV2.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

import fi.hamk.calmfulnessV2.azure.AzureServiceAdapter;
import fi.hamk.calmfulnessV2.azure.AzureTableHandler;
import fi.hamk.calmfulnessV2.helpers.AlertDialogProvider;

public class InitAzure extends AsyncTask<Void, Void, Boolean> {

    private String TAG = InitAzure.class.getName();

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
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
            new AlertDialogProvider().createAndShowDialogFromTask("Azure Init Error", e);
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean state) {
        super.onPostExecute(state);

        AsyncController.azureSuccess(state);
    }
}
