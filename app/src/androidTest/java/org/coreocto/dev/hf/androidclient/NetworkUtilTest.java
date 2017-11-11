package org.coreocto.dev.hf.androidclient;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.coreocto.dev.hf.androidclient.util.NetworkUtil;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class NetworkUtilTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        boolean hostFound = NetworkUtil.isHostFound("www.google.com.hk");

        assertTrue(hostFound);
    }
}
