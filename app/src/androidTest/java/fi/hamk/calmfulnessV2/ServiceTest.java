package fi.hamk.calmfulnessV2;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import fi.hamk.calmfulnessV2.services.LocalService;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ServiceTest {

    private Context appContext;

    @Before
    public void setUp() {
        // Context of the app under test
        appContext = InstrumentationRegistry.getTargetContext();
    }

    @After
    public void breakDown() {
        appContext = null;
    }

    @Test
    public void useAppContext() throws Exception {

        assertEquals("fi.hamk.calmfulnessV2", appContext.getPackageName());
    }

    @Test
    public void startService() {
        appContext.startService(new Intent(appContext, LocalService.class));

        assertEquals(true, isMyServiceRunning(LocalService.class));
    }

    @Test
    public void stopService() {
        appContext.stopService(new Intent(appContext, LocalService.class));

        assertEquals(false, isMyServiceRunning(LocalService.class));
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
