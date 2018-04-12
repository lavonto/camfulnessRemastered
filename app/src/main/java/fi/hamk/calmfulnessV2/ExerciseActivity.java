package fi.hamk.calmfulnessV2;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
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
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import fi.hamk.calmfulnessV2.asyncTasks.AsyncController;
import fi.hamk.calmfulnessV2.azure.AzureServiceAdapter;
import fi.hamk.calmfulnessV2.azure.AzureTableHandler;
import fi.hamk.calmfulnessV2.azure.Exercise;
import fi.hamk.calmfulnessV2.azure.LocationExercise;
import fi.hamk.calmfulnessV2.azure.VisitedList;
import fi.hamk.calmfulnessV2.helpers.AlertDialogProvider;
import fi.hamk.calmfulnessV2.helpers.NotificationProvider;

public class ExerciseActivity extends AppCompatActivity {


    private String savedExerciseId;
    private String youtubeId;

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

        VisitedList.initialize();

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
        if (!AzureServiceAdapter.isInitialized()) {
            new AlertDialogProvider().createAndShowDialog("Azure Init Error", "Azure connection not initialized!\nYou shouldn't be here!");
        } else if (!AzureServiceAdapter.checkLocalStorage()) {
            //Check if local storage is initialized
            new AlertDialogProvider().createAndShowDialog("Offline Storage Error", "Offline storage not initialized!\nYou shouldn't be here!");
            //Check if we are returning from a configuration change
        }

        NotificationProvider.setNotificationSent(false);

        if (savedInstanceState != null) {
            //Set the exercise to the one we left with
            savedExerciseId = savedInstanceState.getString("savedExerciseId");
            restoreExercise(savedExerciseId);
        } else {
            chooseExercise();
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("savedExerciseId", savedExerciseId);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(this, MapsActivity.class));
    }

    public void goBack(final View view) {
        onBackPressed();
    }

    // Fetch exercise from Azure and set the content to UI
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
            }
        }

        if (exercise != null) {
            showExerciseContent(exercise);
        }
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


    private void showExerciseContent(Exercise exercise) {

        savedExerciseId = exercise.getId();

        // Fetch title and content textViews, video link button and image imageView
        final TextView title = findViewById(R.id.textExerciseTitle);
        final TextView content = findViewById(R.id.textExerciseContent);
        final Button videoLink = findViewById(R.id.buttontExerciseVideoLink);
        final ImageView image = findViewById(R.id.imageExerciseImage);


        if (exercise.getPictureUrl() != null) {
            new AsyncController(this, this).downloadImage().execute(exercise.getPictureUrl());
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
            videoLink.setText(getString(R.string.video_button_text));
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

    /**
     * Sets visibility of mProgressBar
     *
     * @param state <tt>True</tt> to show, <tt>False</tt> to hide
     */
    public void setProgressbarState(final boolean state) {
        final ConstraintLayout mLoadingIndicator = findViewById(R.id.loading);
        if (mLoadingIndicator != null) {
            if (state) {
                mLoadingIndicator.setVisibility(ProgressBar.VISIBLE);
            } else {
                mLoadingIndicator.setVisibility(ProgressBar.GONE);
            }
        }
    }
}

