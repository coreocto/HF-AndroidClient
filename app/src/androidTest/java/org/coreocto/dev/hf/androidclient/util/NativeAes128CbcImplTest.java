package org.coreocto.dev.hf.androidclient.util;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Base64;
import android.util.Log;
import org.coreocto.dev.hf.commonlib.util.IAes128Cbc;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class NativeAes128CbcImplTest {

    private static final String TAG = "NativeAes128CbcImplTest";

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        IAes128Cbc aes128Cbc = new NativeAes128CbcImpl();

        String plain = "1234567890123456";
        String key = "1234567890123456";

        for (;;) {

            byte[] encData = aes128Cbc.encrypt(null, key.getBytes(), plain.getBytes());

            Log.d(TAG, Base64.encodeToString(encData, Base64.NO_WRAP));

            byte[] decData = aes128Cbc.decrypt(null, key.getBytes(), encData);

            String decDataStr = new String(decData);

            Log.d(TAG, decDataStr);

            assertEquals(plain, decDataStr);
        }
    }
}
