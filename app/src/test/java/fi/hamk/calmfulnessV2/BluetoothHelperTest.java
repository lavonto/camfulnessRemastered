package fi.hamk.calmfulnessV2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class BluetoothHelperTest {

    BluetoothHelper instance1;
    BluetoothHelper instance2;

    @Test(expected = IllegalStateException.class)
    public void preventReflection() {
        instance1 = new BluetoothHelper();
        instance2 = new BluetoothHelper();
    }

    @Test
    public void sameInstance() {
        instance1 = BluetoothHelper.getInstance();
        instance2 = BluetoothHelper.getInstance();

        assertEquals(instance1, instance2);
    }


}
