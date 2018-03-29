package fi.hamk.calmfulnessV2.asyncTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;

import java.lang.ref.WeakReference;
import java.util.List;

import fi.hamk.calmfulnessV2.MainActivity;
import fi.hamk.calmfulnessV2.azure.AzureServiceAdapter;

/**
 * Controller class for async tasks
 */
public class AsyncController {

    // Objects
    private Context context;
    private Activity activity;
    private Button button;

    /**
     * Constructor of AsyncController. Constructor receives weak references to <tt>Context</tt> and <tt>Activity</tt> of calling Activity to avoid leaking. For more information. See {@link WeakReference}.
     *
     * @param context  Context of calling activity.
     * @param activity Activity of calling activity.
     * @param button   Reference to re-try button of MainActivity
     */
    public AsyncController(Context context, Activity activity, Button button) {
        this.context = context;
        this.activity = activity;
        this.button = button;
    }

    /**
     * Constructor of AsyncController. Constructor receives weak references to <tt>Context</tt> and <tt>Activity</tt> of calling Activity. For more information. See {@link WeakReference}.
     *
     * @param context  Context of calling activity.
     * @param activity Activity of calling activity.
     */
    public AsyncController(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    /**
     * Initializes AzureServiceAdapter, AzureTableHandler  and enables UI buttons on success.
     */
    public AsyncTask<Void, Void, Boolean> initAzure() {

        if (!AzureServiceAdapter.isInitialized()) {

            return new InitAzure(context, activity);

        } else if (!AzureServiceAdapter.checkLocalStorage()) {

            return new InitLocalStorage(context, activity);

        } else if (button.getVisibility() == View.VISIBLE) {

            return new RefreshTables(context, activity);

        } else {
            ((MainActivity) activity).setProgressbarState(false);
        }
        return null;
    }

    /**
     * Returns a new GetRoutePoints task
     *
     * @return {@link GetRoutePoints}
     */
    public GetRoutePoints getRoutePoints(List<String> urls) {
        return new GetRoutePoints(context, activity, urls);
    }
}
