package fi.hamk.calmfulness.asyncTasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

import java.lang.ref.WeakReference;
import java.util.List;

import fi.hamk.calmfulness.ExerciseActivity;
import fi.hamk.calmfulness.MainActivity;
import fi.hamk.calmfulness.MapsActivity;
import fi.hamk.calmfulness.R;
import fi.hamk.calmfulness.azure.AzureServiceAdapter;
import fi.hamk.calmfulness.helpers.AlertDialogProvider;

/**
 * Controller class for async tasks
 */
public class AsyncController {

    private static WeakReference<Context> context;
    private static WeakReference<Activity> activity;

    /**
     * Constructor of {@link AsyncController}
     *
     * @param context  Context of caller
     * @param activity Activity of caller
     */
    public AsyncController(Context context, Activity activity) {
        AsyncController.context = new WeakReference<>(context);
        AsyncController.activity = new WeakReference<>(activity);
    }

    /**
     * Returns weak reference to Context
     *
     * @return {@link WeakReference<Context>} context
     */
    public static WeakReference<Context> getContext() {
        return context;
    }

    /**
     * Returns weak reference to Activity
     *
     * @return {@link WeakReference<Activity>} activity
     */
    public static WeakReference<Activity> getActivity() {
        return activity;
    }

    public static void setContext(Context context) {
        AsyncController.context = new WeakReference<>(context);
    }

    public static void setActivity(Activity activity) {
        AsyncController.activity = new WeakReference<>(activity);
    }

    // Validates WeakReference<Context> context
    private boolean isContextValid() {
        return context.get() != null;
    }

    // Validates WeakReference<Activity> activity
    private boolean isActivityValid() {
        return activity.get() != null;
    }

    /**
     * Initializes AzureServiceAdapter, AzureTableHandler  and enables UI buttons on success.
     *
     * @return a new {@link InitAzure}, {@link InitLocalStorage} or {@link RefreshTables} task
     */
    public AsyncTask<Void, Void, Boolean> initAzure() {

        if (AzureServiceAdapter.isInitialized()) {

            return new InitAzure(this);

        } else if (AzureServiceAdapter.checkLocalStorage()) {

            return new InitLocalStorage(this);

        }
        return new RefreshTables(this);
    }

    /**
     * Returns a new GetRoutePoints task
     *
     * @return {@link GetRoutePoints}
     */
    public GetRoutePoints getRoutePoints() {
        return new GetRoutePoints(this);
    }

    /**
     * Returns a new DownloadImage task
     *
     * @return a new {@link DownloadImage} task
     */
    public DownloadImage downloadImage() {
        return new DownloadImage(this);
    }

    void onPreMainActivityTask(AsyncTask asyncTask) {

        if (isActivityValid()) {
            ((MainActivity) getActivity().get()).setProgressbarState(true);
        } else {
            asyncTask.cancel(true);
        }
    }

    void onPostMainActivityTask(final boolean state) {

        if (isActivityValid()) {
            ((MainActivity) getActivity().get()).setProgressbarState(false);
            ((MainActivity) getActivity().get()).setMenuButtonState(state);
        }
    }

    void onPreMapsActivityTask(AsyncTask asyncTask) {

        if (isActivityValid()) {
            ((MapsActivity) getActivity().get()).setProgressbarState(true);
        } else {
            asyncTask.cancel(true);
        }
    }

    void onPostMapsActivityTask(final List<LatLng> results) {

        if (isActivityValid()) {
            ((MapsActivity) getActivity().get()).setProgressbarState(false);

            if (results != null) {
                ((MapsActivity) getActivity().get()).drawRouteOnMap(results);
            }
        }
    }

    void onPreExerciseActivityTask(AsyncTask asyncTask) {

        if (isActivityValid()) {
            ((ExerciseActivity) getActivity().get()).setProgressbarState(true);
            ((ExerciseActivity) getActivity().get()).setButtonState(false);
        } else {
            asyncTask.cancel(true);
        }
    }

    void onPostExerciseActivityTask(final Bitmap bitmap) {
        if (isActivityValid()) {
            ((ExerciseActivity) getActivity().get()).setProgressbarState(false);
            ((ExerciseActivity) getActivity().get()).setButtonState(true);
            ((ExerciseActivity) getActivity().get()).setSavedBitmap(bitmap);
            ((ExerciseActivity) getActivity().get()).setExerciseImage();
        }
    }

    void onTaskCanceled(String task) {
        Log.d("Task canceled", "Task " + task + " was canceled for unknown reason");
    }

    void onTaskError(final String title, final Exception exception) {

        if (isContextValid()) {
            new AlertDialogProvider(getContext().get()).createAndShowDialog(title, exception.toString());
        }

    }

}
