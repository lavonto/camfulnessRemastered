package fi.hamk.calmfulnessV2;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import fi.hamk.calmfulnessV2.azure.AzureServiceAdapter;
import fi.hamk.calmfulnessV2.azure.AzureTableHandler;
import fi.hamk.calmfulnessV2.azure.Exercise;
import fi.hamk.calmfulnessV2.azure.VisitedList;
import fi.hamk.calmfulnessV2.helpers.AlertDialogProvider;
import fi.hamk.calmfulnessV2.helpers.UserNotification;

public class ExerciseActivity extends AppCompatActivity {
    private static final String TAG = ExerciseActivity.class.getName();

    /**
     * Helper for creating alert dialogs
     */
    private AlertDialogProvider alertDialogProvider;

    /**
     * Index of the current exercise
     */
    private static int mCurIndex;

    /**
     * Key for the stored index
     */
    private static final String STATE_INDEX = "exerciseIndex";

    /**
     * Random number generator for selecting Exercise
     */
    private static final Random random = new Random();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mind_exercise);

        alertDialogProvider = new AlertDialogProvider(this);

        // Gets custom toolbar and sets it as support actionbar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_main));

        //Check if there already is an AzureServiceAdapter instance
        if (!AzureServiceAdapter.isInitialized()) {
            Log.e(TAG, "AzureServiceAdapter not initialized");
            alertDialogProvider.createAndShowDialog("Azure Init Error", "Azure connection not initialized!\nYou shouldn't be here!");
        }
        //Check if local storage is initialized
        else if (!AzureServiceAdapter.checkLocalStorage()) {
            Log.e(TAG, "Offline Storage not initialized");
            alertDialogProvider.createAndShowDialog("Offline Storage Error", "Offline storage not initialized!\nYou shouldn't be here!");
        //Check if we are returning from a configuration change
        } else if (savedInstanceState != null) {
            //Set the exercise to the one we left with
            mCurIndex = savedInstanceState.getInt(STATE_INDEX);
            setExercise(mCurIndex);
        }
        else
            setExercise();

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

        //Cancel the notification when user opens the Exercise
        UserNotification.cancel(this);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(STATE_INDEX,mCurIndex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //If user closes app while notification is sent, cancel it
        if (UserNotification.isNotificationSent()) {
            UserNotification.cancel(this);
        }
    }

    @Override
    public void onBackPressed() {
        Intent goBackIntent = new Intent(this, MapsActivity.class);
        goBackIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(goBackIntent);

        super.onBackPressed();
    }

    public void goBack(final View view) {
        Intent goBackIntent = new Intent(this, MapsActivity.class);
        goBackIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(goBackIntent);
    }

    /**
     * Fetch exercise from Azure and set the content to UI
     */
    private void setExercise() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //Fetch a list of all exercises
                    final List<Exercise> result = AzureTableHandler.getExercisesFromDb();
                    //Generate a random index number
                    mCurIndex = random.nextInt(result.size());

                    //If we have already visited an exercise
                    if (!VisitedList.isNull()) {

                        //If we have seen all exercises, clear list of visited indexes
                        if (VisitedList.getVisited().size() >= result.size())
                            VisitedList.clearVisited();

                        //Do re-rolls until we get an index for an exercise we haven't visited
                        while (VisitedList.getVisited().contains(mCurIndex)) {
                            mCurIndex = random.nextInt(result.size());
                        }
                    }
                    //If we haven't seen any exercises, initialize the list
                    else
                        VisitedList.initialize();

                    //Pull the exercise from result list
                    final Exercise exercise = result.get(mCurIndex);

                    //Assign the exercise to UI elements
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final TextView mTextExerciseTitle = findViewById(R.id.textExerciseTitle);
                            final TextView mTextExerciseContent = findViewById(R.id.textExerciseContent);
                            mTextExerciseTitle.setText(exercise.getExerciseTitle());
                            mTextExerciseContent.setText(exercise.getExerciseContent());
                        }
                    });

                    //Add the index of the visited exercise to our list
                    VisitedList.addVisited(mCurIndex);
                } catch (Exception e) {
                    Log.e(TAG, e.toString(), e);
                    alertDialogProvider.createAndShowDialogFromTask("Azure Query Error", e);
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                showProgressbar(true);
            }

            @Override
            protected void onPostExecute(Void voids) {
                showProgressbar(false);
            }
        }.execute();
    }

    /**
     * Fetch Exercise from Azure and set content to UI
     * @param index Index of the exercise
     */
    private void setExercise(final int index){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //Fetch a list of all exercises
                    final List<Exercise> result = AzureTableHandler.getExercisesFromDb();

                    //Pull the exercise from result list
                    final Exercise exercise = result.get(index);

                    //Assign the exercise to UI elements
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final TextView mTextExerciseTitle = findViewById(R.id.textExerciseTitle);
                            final TextView mTextExerciseContent = findViewById(R.id.textExerciseContent);
                            mTextExerciseTitle.setText(exercise.getExerciseTitle());
                            mTextExerciseContent.setText(exercise.getExerciseContent());
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, e.toString(), e);
                    alertDialogProvider.createAndShowDialogFromTask("Azure Query Error", e);
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                showProgressbar(true);
            }

            @Override
            protected void onPostExecute(Void voids) {
                showProgressbar(false);
            }
        }.execute();
    }

    @Override
    protected void onResume() {

        if (UserNotification.isNotificationSent()) {
            UserNotification.cancel(this);
        }
        super.onResume();
    }

    /**
     * Sets visibility of mProgressBar
     *
     * @param state <tt>True</tt> to show, <tt>False</tt> to hide
     */
    private void showProgressbar(final boolean state) {
        final ConstraintLayout mProgressBar = findViewById(R.id.loading);
        if (state) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
                }
            });
        }
    }
}
