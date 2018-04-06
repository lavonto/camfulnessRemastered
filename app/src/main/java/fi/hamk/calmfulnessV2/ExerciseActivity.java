package fi.hamk.calmfulnessV2;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import fi.hamk.calmfulnessV2.azure.AzureServiceAdapter;
import fi.hamk.calmfulnessV2.azure.AzureTableHandler;
import fi.hamk.calmfulnessV2.azure.Exercise;
import fi.hamk.calmfulnessV2.azure.LocationExercise;
import fi.hamk.calmfulnessV2.azure.VisitedList;
import fi.hamk.calmfulnessV2.helpers.AlertDialogProvider;

public class ExerciseActivity extends AppCompatActivity {


    private String exerciseId;
    private String youtubeId;

    private static String TAG = ExerciseActivity.class.getName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mind_exercise);

        // Gets custom toolbar and sets it as support actionbar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_main));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        if (!AzureServiceAdapter.isInitialized()) {
            new AlertDialogProvider().createAndShowDialog("Azure Init Error", "Azure connection not initialized!\nYou shouldn't be here!");
        } else if (!AzureServiceAdapter.checkLocalStorage()) {
            //Check if local storage is initialized
            new AlertDialogProvider().createAndShowDialog("Offline Storage Error", "Offline storage not initialized!\nYou shouldn't be here!");
            //Check if we are returning from a configuration change
        }

        if (savedInstanceState != null) {
            //Set the exercise to the one we left with
            exerciseId = savedInstanceState.getString("exerciseIndex");
        }
        showExercise(exerciseId);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("exerciseIndex", exerciseId);
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
    private void showExercise(String exerciseId) {

        final String locationId = getIntent().getStringExtra("location");

        List<Exercise> exercises = null;
        List<LocationExercise> locationExercise = null;

        Exercise exercise = null;

        try {
            if (locationId != null) {
                locationExercise = AzureTableHandler.getLocationExerciseFromDb(locationId);
            }
            exercises = AzureTableHandler.getExercisesFromDb();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if (exerciseId == null) {
            if (locationExercise != null) {
                try {
                    exercise = AzureTableHandler.lookUpExerciseFromDb(locationExercise.get(0).getExercise());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if (exercise == null) {

            //Generate a random index number
            final Random random = new Random();
            int index = random.nextInt(exercises.size());

            //If we have already visited an exercise
            if (!VisitedList.isNull()) {

                //If we have seen all exercises, clear list of visited indexes
                if (VisitedList.getVisited().size() >= exercises.size())
                    VisitedList.clearVisited();

                //Do re-rolls until we get an index for an exercise we haven't visited
                while (VisitedList.getVisited().contains(index)) {
                    index = random.nextInt(exercises.size());
                }
            }
            exercise = exercises.get(index);
        }

        // Fetch title and content textViews and set
        final TextView title = findViewById(R.id.textExerciseTitle);
        final TextView content = findViewById(R.id.textExerciseContent);
        final TextView videoLink = findViewById(R.id.buttontExerciseVideoLink);
        final ImageView image = findViewById(R.id.imageExerciseImage);


        if (exercise != null) {

            if (exercise.getPictureUrl() == null) { // TODO: change to !=
                image.setImageResource(R.drawable.meadow_land);
            } else {
                image.setVisibility(View.GONE);
            }

            title.setText(exercise.getTitleFi());
            content.setText(exercise.getTextFi());

            if (exercise.getVideoUrl() == null) {
                youtubeId = exercise.getVideoUrl();
                videoLink.setText(/*exercise.getVideoUrl()*/ "Jargonia");
            } else {
                videoLink.setVisibility(View.GONE);
            }

        }
    }

    public void watchVideoOnYoutube(View view) {

        try {
            // Try opening video on YouTube app
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + "dQw4w9WgXcQ" /*youtubeId*/)));
        } catch (ActivityNotFoundException e) {
            // If YouTube app was not found, open video on a web browser
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + "dQw4w9WgXcQ" /*youtubeId*/)));
        }
    }
}
