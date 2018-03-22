package fi.hamk.calmfulnessV2.asyncTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;

import fi.hamk.calmfulnessV2.azure.AzureServiceAdapter;


public class AsyncController {

    private static String TAG = AsyncController.class.getName();

    private Context context;
    private Button button;

    private static boolean result = false;

    public AsyncController(Context context, Button button) {
        this.context = context;
        this.button = button;
    }

   public boolean InitAzure() {
       Log.d(TAG, "InitAzure()");

       if (!AzureServiceAdapter.isInitialized()) {
               //Initialize Adapter
               try {
                   AzureServiceAdapter.Initialize(context);
                   result = true;
               } catch (MalformedURLException e) {
                   e.printStackTrace();
               }

               new InitAzure().execute();

       } else if (!AzureServiceAdapter.checkLocalStorage()) {

               new InitLocalStorage().execute();
       } else if (button.getVisibility() == View.VISIBLE) {

               new InitTables().execute();

       }
       Log.d(TAG, "Returning result: " + result);
       return result;
   }

    static void azureSuccess(final boolean state) {
        result = state;
    }
}
