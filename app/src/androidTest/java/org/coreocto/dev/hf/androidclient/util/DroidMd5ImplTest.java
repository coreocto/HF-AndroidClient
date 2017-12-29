package org.coreocto.dev.hf.androidclient.util;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Base64;
import android.util.Log;
import org.apache.commons.lang.RandomStringUtils;
import org.coreocto.dev.hf.androidclient.crypto.AndroidMd5Impl;
import org.coreocto.dev.hf.commonlib.crypto.IHashFunc;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class DroidMd5ImplTest {

    private static final String TAG = "DroidMd5ImplTest";

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        IHashFunc md5 = new AndroidMd5Impl();

        List<String> randomStrList = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            randomStrList.add(RandomStringUtils.random(20));
        }

        long startTime = System.currentTimeMillis();
        for (String randomStr : randomStrList) {
            byte[] data = md5.getHash(randomStr.getBytes());
            Log.i(TAG, Base64.encodeToString(data, Base64.NO_WRAP));
        }
        long endTime = System.currentTimeMillis();
        Log.d(TAG, "elapsed time = " + (endTime - startTime) + "ms");
    }
}
