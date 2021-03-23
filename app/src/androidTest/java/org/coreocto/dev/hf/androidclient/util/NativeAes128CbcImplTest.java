package org.coreocto.dev.hf.androidclient.util;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Base64;
import android.util.Log;
import org.apache.commons.lang.RandomStringUtils;
import org.coreocto.dev.hf.androidclient.crypto.NativeAes128CbcImpl;
import org.coreocto.dev.hf.commonlib.crypto.IBlockCipherCbc;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class NativeAes128CbcImplTest {

    private static final String TAG = "NativeAes128CbcImplTest";

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        IBlockCipherCbc aes128Cbc = new NativeAes128CbcImpl();

        List<String> randomStrList = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            randomStrList.add(RandomStringUtils.random(20));
        }

        byte[] key = new byte[16];
        byte[] iv = new byte[16];

        long startTime = System.currentTimeMillis();
        for (String randomStr : randomStrList) {
            byte[] data = randomStr.getBytes();
            byte[] encData = aes128Cbc.encrypt(iv, key, data);
            Log.i(TAG, Base64.encodeToString(encData, Base64.NO_WRAP));
        }
        long endTime = System.currentTimeMillis();
        Log.d(TAG, "elapsed time = " + (endTime - startTime) + "ms");
    }
}
