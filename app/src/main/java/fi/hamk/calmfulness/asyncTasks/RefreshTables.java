package fi.hamk.calmfulness.asyncTasks;

import android.os.AsyncTask;

import fi.hamk.calmfulness.azure.AzureTableHandler;


public class RefreshTables extends AsyncTask<Void, Void, Boolean> {

    private AsyncController asyncController;

    private Exception exception;

    RefreshTables(AsyncController asyncController) {
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
                AzureTableHandler.refreshTables();
            } catch (Exception exception) {
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
            asyncController.onTaskError("Refresh Tables Error", exception);
        }

        asyncController.onPostMainActivityTask(state);
    }

    @Override
    protected void onCancelled(Boolean state) {
        super.onCancelled(state);

        asyncController.onTaskCanceled(RefreshTables.class.getName());
    }
}
