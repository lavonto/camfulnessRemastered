package fi.hamk.calmfulnessV2.helpers;

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
    private Bitmap retainedBitmap;

    // This method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment
        setRetainInstance(true);
    }

    /**
     * Sets an object of Bitmap to be retained
     *
     * @return  Bitmap
     */
    public Bitmap getRetainedBitmap() {
        return retainedBitmap;
    }

    /**
     * Sets an object of Bitmap to be retained
     *
     * @param  retainedBitmap Bitamp to be retained
     */
    public void setRetainedBitmap(Bitmap retainedBitmap) {
        this.retainedBitmap = retainedBitmap;
    }
}
