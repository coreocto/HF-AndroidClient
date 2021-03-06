package org.coreocto.dev.hf.androidclient.util;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Base64;
import android.util.Log;
import org.apache.commons.lang.RandomStringUtils;
import org.coreocto.dev.hf.androidclient.crypto.AndroidAes128CtrImpl;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class DroidAes128CtrImplTest {

    private static final String TAG = "DroidAes128CtrImplTest";

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        AndroidAes128CtrImpl aes128Ctr = new AndroidAes128CtrImpl();

        List<String> randomStrList = new ArrayList<>();

        String longStr = "";

        for (int i = 0; i < 100; i++) {
            String s = RandomStringUtils.random(20);
            randomStrList.add(s);
            longStr += s;
        }

        byte[] key = new byte[16];
        byte[] iv = new byte[16];

        {
            long startTime = System.currentTimeMillis();
            for (String randomStr : randomStrList) {
                byte[] data = randomStr.getBytes();
                byte[] encData = aes128Ctr.encrypt(iv, key, data);
                Log.i(TAG, Base64.encodeToString(encData, Base64.NO_WRAP));
            }
            long endTime = System.currentTimeMillis();
            Log.d(TAG, "elapsed time = " + (endTime - startTime) + "ms");
        }

        {
            long startTime = System.currentTimeMillis();
            byte[] data = longStr.getBytes();
            byte[] encData = aes128Ctr.encrypt(iv, key, data);
            Log.i(TAG, Base64.encodeToString(encData, Base64.NO_WRAP));
            long endTime = System.currentTimeMillis();
            Log.d(TAG, "elapsed time = " + (endTime - startTime) + "ms");
        }
    }
}
