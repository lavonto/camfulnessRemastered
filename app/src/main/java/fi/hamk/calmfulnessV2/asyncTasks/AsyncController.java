package fi.hamk.calmfulnessV2.asyncTasks;

import android.app.Activity;
import android.content.Context;
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

    // Log tag
    private static String TAG = AsyncController.class.getName();

    // Objects
    private Context context;
    private Activity activity;
    private Button button;

    /**
     * Constructor of AsyncController. Constructor receives weak references to <tt>Context</tt> and <tt>Activity</tt> of calling Activity to avoid leaking. For more information. See {@link WeakReference}.
     * @param context Context of calling activity.
     * @param activity Activity of calling activity.
     * @param button Reference to re-try button of MainActivity
     */
    public AsyncController(Context context,  Activity activity, Button button) {
        this.context = context;
        this.activity = activity;
        this.button = button;
    }

    /**
     *  Constructor of AsyncController. Constructor receives weak references to <tt>Context</tt> and <tt>Activity</tt> of calling Activity. For more information. See {@link WeakReference}.
     * @param context Context of calling activity.
     * @param activity Activity of calling activity.
     */
    public AsyncController(Context context,  Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    /**
     * Initializes AzureServiceAdapter and AzureTableHandler, and enables UI buttons on success.
     */
   public void initAzure() {

       if (!AzureServiceAdapter.isInitialized()) {
               new InitAzure(context, activity).execute();

       } else if (!AzureServiceAdapter.checkLocalStorage()) {

               new InitLocalStorage(context, activity).execute();
       } else if (button.getVisibility() == View.VISIBLE) {

               new RefreshTables(context, activity).execute();
       } else {
           ((MainActivity)activity).setProgressbarState(false);
       }
   }

    /**
     * Returns a new InitRouteContainer task
     * @return {@link InitRouteContainer}
     */
   public InitRouteContainer initRouteContainer() {
      return new InitRouteContainer(context, activity);
   }

    /**
     * Returns a new GetRoutePoints task
     * @return {@link GetRoutePoints}
     */
   public GetRoutePoints getRoutePoints(List<String> urls) {
       return new GetRoutePoints(context, activity, urls);
   }
}
