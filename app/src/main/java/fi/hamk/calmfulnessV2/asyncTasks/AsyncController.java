package fi.hamk.calmfulnessV2.asyncTasks;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import java.lang.ref.WeakReference;

import fi.hamk.calmfulnessV2.MainActivity;
import fi.hamk.calmfulnessV2.azure.AzureServiceAdapter;

/**
 * Controller class for async tasks
 */
public class AsyncController {

    // Log tag
    private static String TAG = AsyncController.class.getName();

    // Objects
    private WeakReference<Context> weakContext;
    private WeakReference<Activity> weakActivity;
    private Button button;

    /**
     * Constructor of AsyncController. Constructor receives weak references to <tt>Context</tt> and <tt>Activity</tt> of calling Activity to avoid leaking. For more information. See {@link WeakReference}.
     * @param weakContext Weak reference to context of calling activity.
     * @param weakActivity Weak reference to context of calling activity.
     * @param button Reference to re-try button of MainActivity
     */
    public AsyncController(WeakReference<Context> weakContext,  WeakReference<Activity> weakActivity, Button button) {
        this.weakContext = weakContext;
        this.weakActivity = weakActivity;
        this.button = button;
    }

    /**
     *  Constructor of AsyncController. Constructor receives weak references to <tt>Context</tt> and <tt>Activity</tt> of calling Activity. For more information. See {@link WeakReference}.
     * @param weakContext Weak reference to context of calling activity.
     * @param weakActivity Weak reference to context of calling activity.
     */
    public AsyncController(WeakReference<Context> weakContext,  WeakReference<Activity> weakActivity) {
        this.weakContext = weakContext;
        this.weakActivity = weakActivity;
    }

    /**
     * Initializes AzureServiceAdapter and AzureTableHandler, and enables UI buttons on success.
     */
   public void initAzure() {

       if (!AzureServiceAdapter.isInitialized()) {
               new InitAzure(weakContext, weakActivity).execute();

       } else if (!AzureServiceAdapter.checkLocalStorage()) {

               new InitLocalStorage(weakContext, weakActivity).execute();
       } else if (button.getVisibility() == View.VISIBLE) {

               new RefreshTables(weakContext, weakActivity).execute();
       } else {
           ((MainActivity)weakActivity.get()).showProgressbar(false);
       }
   }

}
