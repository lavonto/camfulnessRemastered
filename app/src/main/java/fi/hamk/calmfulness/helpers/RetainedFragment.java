package fi.hamk.calmfulness.helpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * This blank fragment is used to retain objects an example Context or Activity when orientation changes
 */
public class RetainedFragment extends Fragment {

    // Objects we want to retain
    private Context retainedContext;
    private Activity retainedActivity;

    public Context getRetainedContext() {
        return retainedContext;
    }

    public void setRetainedContext(Context retainedContext) {
        this.retainedContext = retainedContext;
    }

    public Activity getRetainedActivity() {
        return retainedActivity;
    }

    public void setRetainedActivity(Activity retainedActivity) {
        this.retainedActivity = retainedActivity;
    }

    // This method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment
        setRetainInstance(true);
    }

}
