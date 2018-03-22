package fi.hamk.calmfulnessV2.asyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import fi.hamk.calmfulnessV2.azure.AzureTableHandler;
import fi.hamk.calmfulnessV2.helpers.AlertDialogProvider;

public class InitLocalStorage extends AsyncTask<Void, Void, Boolean> {

    private String TAG = InitLocalStorage.class.getName();

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            //Initialize local storage
            AzureTableHandler.initLocalStorage();
            //Populate the created tables
            AzureTableHandler.refreshTables();
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
            new AlertDialogProvider().createAndShowDialogFromTask("Local Init Error", e);
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
