package fi.hamk.calmfulness;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import fi.hamk.calmfulness.asyncTasks.AsyncController;
import fi.hamk.calmfulness.azure.AzureServiceAdapter;
import fi.hamk.calmfulness.azure.AzureTableHandler;
import fi.hamk.calmfulness.azure.Exercise;
import fi.hamk.calmfulness.azure.LocationExercise;
import fi.hamk.calmfulness.helpers.AlertDialogProvider;
import fi.hamk.calmfulness.helpers.NotificationProvider;
import fi.hamk.calmfulness.helpers.RetainedFragment;

public class ExerciseActivity extends AppCompatActivity {


    private static final String TAG_RETAINED_FRAGMENT = "RetainedFragment";

    private RetainedFragment retainedFragment;

    private String savedExerciseId;
    private String youtubeId;

    public void setRetainedBitmap(Bitmap bitmap) {
        retainedFragment.setRetainedBitmap(bitmap);
    }

    private static List<String> visitedList = new ArrayList<>();

    final private static String TAG = ExerciseActivity.class.getName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mind_exercise);

        // Gets custom toolbar and sets it as support actionbar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_main));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Find the retained fragment on activity restarts
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        retainedFragment = (RetainedFragment) fragmentManager.findFragmentByTag(TAG_RETAINED_FRAGMENT);

        // Create the fragment and data the first time
        if (retainedFragment == null) {
            // Add the fragment
            retainedFragment = new RetainedFragment();
            fragmentManager.beginTransaction().add(retainedFragment, TAG_RETAINED_FRAGMENT).commit();
        }

        final View decorView = getWindow().getDecorView();
        //Activity's root View. Can also be root View of your layout (preferably)
        final ViewGroup rootView = decorView.findViewById(android.R.id.content);
        //set background, if your root layout doesn't have one
        final Drawable windowBackground = decorView.getBackground();
        //Setup the BlurView
        final BlurView mBlurView = findViewById(R.id.blur_view);
        mBlurView.setupWith(rootView)
                .windowBackground(windowBackground)
                .blurAlgorithm(new RenderScriptBlur(this))
                .blurRadius(1.5f);
        //Disable update of the BlurView
        mBlurView.setBlurAutoUpdate(false);

        //Check if there already is an AzureServiceAdapter instance
        if (AzureServiceAdapter.isInitialized()) {
            new AlertDialogProvider().createAndShowDialog("Azure Init Error", "Azure connection not initialized!\nYou shouldn't be here!");
        } else if (AzureServiceAdapter.checkLocalStorage()) {
            //Check if local storage is initialized
            new AlertDialogProvider().createAndShowDialog("Offline Storage Error", "Offline storage not initialized!\nYou shouldn't be here!");
            //Check if we are returning from a configuration change
        }

        if (savedInstanceState != null) {
            // Restore the exercise that was showing when this activity was destroyed
            restoreExercise(savedInstanceState.getString("savedExerciseId"));
        } else {
            // Choose new exercise
            chooseExercise();
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        // Store exercise id so it can be restored
        outState.putString("savedExerciseId", savedExerciseId);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(this, MapsActivity.class));
    }

    public void onBackPressed(final View view) {
        onBackPressed();
    }

    private void restoreExercise(final String savedExerciseId) {

        // If this activity was destroyed and restored, restore the exercise that was previously shown
        try {
            final Exercise exercise = AzureTableHandler.lookUpExerciseFromDb(savedExerciseId);
            showExerciseContent(exercise);
        } catch (ExecutionException | InterruptedException e) {
            e.getMessage();
        }
    }

    private void chooseExercise() {

        String locationId = getIntent().getStringExtra("locationId");

        List<LocationExercise> locationExercises = null;
        List<Exercise> exercises = null;
        Exercise exercise = null;

        try {
            locationExercises = AzureTableHandler.getLocationFieldInLocationExerciseTableFromDb(locationId);
            if (locationExercises.size() > 0) {
                exercise = AzureTableHandler.lookUpExerciseFromDb(locationExercises.get(0).getExercise());
            } else {
                exercises = AzureTableHandler.getAllExercisesFromDb();
                locationExercises = AzureTableHandler.getAllLocationExercisesFromDb();

            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if (exercise == null) {
            if (exercises != null && locationExercises != null) {
                Random random = new Random();

                if (visitedList.size() == exercises.size() - locationExercises.size()) {
                    Log.d(TAG, "Visited list reached maximum size: " + visitedList.size() + " of " + (exercises.size() - locationExercises.size()));
                    visitedList.clear();
                }

                do {
                    int i = random.nextInt(exercises.size());
                    String id = exercises.get(i).getId();
                    try {
                        locationExercises = AzureTableHandler.getExerciseFieldInLocationExerciseTableFromDb(id);

                        if (locationExercises.size() == 0 && !visitedList.contains(id)) {
                            exercise = exercises.get(i);
                        }
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (exercise == null);
                visitedList.add(exercise.getId());
                Log.d(TAG, "Added new id to list: " + exercise.getId());
            }
        }

        if (exercise != null) {
            showExerciseContent(exercise);
        }
    }

    private void showExerciseContent(Exercise exercise) {
        NotificationProvider.setNotificationSent(false);

        savedExerciseId = exercise.getId();

        // Fetch title and content textViews, video link button and image imageView
        final TextView title = findViewById(R.id.textExerciseTitle);
        final TextView content = findViewById(R.id.textExerciseContent);
        final Button videoLink = findViewById(R.id.buttontExerciseVideoLink);
        final ImageView image = findViewById(R.id.imageExerciseImage);


        if (exercise.getPictureUrl() != null) {
            if (retainedFragment.getRetainedBitmap() == null) {
                new AsyncController(this, this).downloadImage().execute(exercise.getPictureUrl());
            } else {
                image.setImageBitmap(retainedFragment.getRetainedBitmap());
            }
        } else {
            image.setVisibility(View.GONE);
        }

        if (Locale.getDefault().getDisplayLanguage().equals(Locale.ENGLISH.toString())) {
            title.setText(exercise.getTitleEn());
            content.setText(exercise.getTextEn());
        } else {
            title.setText(exercise.getTitleFi());
            content.setText(exercise.getTextFi());
        }

        if (exercise.getVideoId() != null) {
            youtubeId = exercise.getVideoId();
            videoLink.setText(getString(R.string.button_watch));
        } else {
            videoLink.setVisibility(View.GONE);
        }

    }

    public void watchVideoOnYouTube(final View view) {

        try {
            // Try opening video on YouTube app
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtubeId)));
        } catch (ActivityNotFoundException e) {
            // If YouTube app was not found, open video on a web browser
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + youtubeId)));
        }
    }

    public void setButtonState(boolean state) {

        if (state) {
            findViewById(R.id.fabExercise).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.fabExercise).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Sets visibility of mProgressBar
     *
     * @param state <tt>True</tt> to show, <tt>False</tt> to hide
     */
    public void setProgressbarState(final boolean state) {

        if (state) {
            findViewById(R.id.loading).setVisibility(ProgressBar.VISIBLE);
        } else {
            findViewById(R.id.loading).setVisibility(ProgressBar.GONE);
        }
    }
}
