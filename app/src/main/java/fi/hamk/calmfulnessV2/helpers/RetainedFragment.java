package fi.hamk.calmfulnessV2.helpers;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * This blank fragment is used to retain objects during configuration changes for example when orientation changes
 */
public class RetainedFragment extends Fragment {

    // Objects we want to retain
    private Activity retainedActivity;
    private Context retainedContext;

    // This method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment
        setRetainInstance(true);
    }

    /**
     * Returns the object of retained Activity
     * @return Activity
     */
    public Activity getRetainedActivity() {
        return retainedActivity;
    }

    /**
     * Sets an object of Activity to be retained
     * @param retainedActivity Activity
     */
    public void setRetainedActivity(Activity retainedActivity) {
        this.retainedActivity = retainedActivity;
    }

    /**
     * Returns the object of retained Context
     * @return Context
     */
    public Context getRetainedContext() {
        return retainedContext;
    }

    /**
     * Sets an object of Context to be retained
     * @param retainedContext Context
     */
    public void setRetainedContext(Context retainedContext) {
        this.retainedContext = retainedContext;
    }
}
