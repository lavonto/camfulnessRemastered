package fi.hamk.calmfulness.asyncTasks;

import android.os.AsyncTask;

import java.util.Objects;

import fi.hamk.calmfulness.azure.AzureServiceAdapter;
import fi.hamk.calmfulness.azure.AzureTableHandler;

/**
 * Async task to initialize AzureTableHandler
 */
public class InitAzure extends AsyncTask<Void, Void, Boolean> {

    private AsyncController asyncController;

    // Exceptions
    private Exception exception = new Exception("This is test exception"); // TODO: Remove before release

    InitAzure(AsyncController asyncController) {
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
                try {
                    AzureServiceAdapter.Initialize(Objects.requireNonNull(AsyncController.getContext().get()));
                } catch (NullPointerException e) {
                    this.exception = e;
                    return false;
                }

                //Initialize TableHandler with AzureServiceAdapter instance
                AzureTableHandler.Initialize(AzureServiceAdapter.getInstance());

                if (AzureServiceAdapter.checkLocalStorage()) {
                    //Initialize local storage
                    AzureTableHandler.initLocalStorage();
                    //Populate the created tables
                    AzureTableHandler.refreshTables();
                }

            } catch (Exception exception) {
                this.exception = exception;
                return false;
            }
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean state) {
        super.onPostExecute(state);

        if (exception != null) {
            asyncController.onTaskError("Init Azure Error", exception);
        }
        asyncController.onPostMainActivityTask(state);
    }


    @Override
    protected void onCancelled(Boolean state) {
        super.onCancelled(state);

        asyncController.onTaskCanceled(InitAzure.class.getName());
    }
}
