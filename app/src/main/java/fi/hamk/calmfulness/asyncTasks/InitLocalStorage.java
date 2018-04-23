package fi.hamk.calmfulness.asyncTasks;

import android.os.AsyncTask;

import fi.hamk.calmfulness.azure.AzureTableHandler;

public class InitLocalStorage extends AsyncTask<Void, Void, Boolean> {

    private AsyncController asyncController;

    // Exceptions
    private Exception exception;

    InitLocalStorage(AsyncController asyncController) {
        this.asyncController = asyncController;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        asyncController.onPreMainActivityTask(this);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (!isCancelled()) {
            try {
                //Initialize local storage
                AzureTableHandler.initLocalStorage();
                //Populate the created tables
                AzureTableHandler.refreshTables();
            } catch (Exception exception) {
                // Store value of caught error for later use
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
            asyncController.onTaskError("Local Storage Error", exception);
        }

        asyncController.onPostMainActivityTask(state);
    }

    @Override
    protected void onCancelled(Boolean state) {
        super.onCancelled(state);

        asyncController.onTaskCanceled(InitLocalStorage.class.getName());
    }
}
