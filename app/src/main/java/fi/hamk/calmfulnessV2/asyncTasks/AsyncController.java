package fi.hamk.calmfulnessV2.asyncTasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

import java.lang.ref.WeakReference;
import java.util.List;

import fi.hamk.calmfulnessV2.ExerciseActivity;
import fi.hamk.calmfulnessV2.MainActivity;
import fi.hamk.calmfulnessV2.MapsActivity;
import fi.hamk.calmfulnessV2.R;
import fi.hamk.calmfulnessV2.azure.AzureServiceAdapter;
import fi.hamk.calmfulnessV2.azure.Exercise;
import fi.hamk.calmfulnessV2.helpers.AlertDialogProvider;

/**
 * Controller class for async tasks
 */
public class AsyncController {

    // Log tag
    private String TAG = AsyncController.class.getName();

    private static WeakReference<Context> context;
    private static WeakReference<Activity> activity;


    public AsyncController(Context context, Activity activity) {
        AsyncController.context = new WeakReference<>(context);
        AsyncController.activity = new WeakReference<>(activity);
    }

    public static WeakReference<Context> getContext() {
        return context;
    }

    public static WeakReference<Activity> getActivity() {
        return activity;
    }

    public static void setContext(Context context) {
        AsyncController.context = new WeakReference<>(context);
    }

    public static void setActivity(Activity activity) {
        AsyncController.activity = new WeakReference<>(activity);
    }

    private boolean isContextValid() {
        return context.get() != null;
    }

    private boolean isActivityValid() {
        return activity.get() != null;
    }

    /**
     * Initializes AzureServiceAdapter, AzureTableHandler  and enables UI buttons on success.
     */
    public AsyncTask<Void, Void, Boolean> initAzure() {

        if (!AzureServiceAdapter.isInitialized()) {

            return new InitAzure(this);

        } else if (!AzureServiceAdapter.checkLocalStorage()) {

            return new InitLocalStorage(this);

        } else if (getActivity().get().findViewById(R.id.btnRetry).getVisibility() == View.VISIBLE) {

            return new RefreshTables(this);

        } else if (isActivityValid()) {
            ((MainActivity) getActivity().get()).setProgressbarState(false);
        }
        return null;
    }

    /**
     * Returns a new GetRoutePoints task
     *
     * @return {@link GetRoutePoints}
     */
    public GetRoutePoints getRoutePoints() {
        return new GetRoutePoints(this);
    }

    public DownloadImage downloadImage() {
        return new DownloadImage(this);
    }

    // MainActivity Task Methods
    public void onPreMainActivityTask(AsyncTask asyncTask) {

        if (isActivityValid()) {
            ((MainActivity) getActivity().get()).setProgressbarState(true);
        } else {
            asyncTask.cancel(true);
        }
    }

    public void onPostMainActivityTask(final boolean state) {

        if (isActivityValid()) {
            ((MainActivity) getActivity().get()).setProgressbarState(false);
            ((MainActivity) getActivity().get()).setMenuButtonState(state);
        }
    }

    // MapsActivity Task Methods
    public void onPreMapsActivityTask(AsyncTask asyncTask) {

        if (isActivityValid()) {
            ((MapsActivity) getActivity().get()).setProgressbarState(true);
        } else {
            asyncTask.cancel(true);
        }
    }

    public void onPostMapsActivityTask(final boolean state, final List<LatLng> results) {

        if (isActivityValid()){
            ((MapsActivity) getActivity().get()).setProgressbarState(false);
            ((MapsActivity) getActivity().get()).drawRouteOnMap(results);
        }
    }

    // ExerciseActivity Task Methods
    public void onPreExerciseActivityTask(AsyncTask asyncTask) {

        if (isActivityValid()) {
            ((ExerciseActivity) getActivity().get()).setProgressbarState(true);
        } else {
            asyncTask.cancel(true);
        }
    }

    public void onPostExerciseActivityTask(final boolean state, final Bitmap bitmap) {

        final ImageView image = ((ExerciseActivity)getActivity().get()).findViewById(R.id.imageExerciseImage);

        if (isActivityValid()) {
            ((ExerciseActivity) getActivity().get()).setProgressbarState(false);
        }

        if (image != null) {
            image.setImageBitmap(bitmap);
        }
    }

    // General Task methods
    public void onTaskCanceled(String task) {
        Log.e(TAG, "Task " + task + " canceled");
    }

    public void onTaskError(final String title, final Exception exception) {

        if (exception != null && isContextValid()) {
            Log.e(title, exception.getMessage());
            new AlertDialogProvider(getContext().get()).createAndShowDialog(title, exception.getMessage());
        }

    }

}
