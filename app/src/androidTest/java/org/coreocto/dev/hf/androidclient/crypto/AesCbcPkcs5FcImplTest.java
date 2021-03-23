package org.coreocto.dev.hf.androidclient.crypto;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.coreocto.dev.hf.commonlib.crypto.IFileCipher;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AesCbcPkcs5FcImplTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        final File extStor = Environment.getExternalStorageDirectory();
        File dataFile = new File(extStor, "adobe5.txt");
        File encDataFile = new File(extStor, "adobe5-enc.txt");
        File decDataFile = new File(extStor, "adobe5-dec.txt");

        byte[] key = new byte[16];
        byte[] iv = new byte[16];

        IFileCipher fileCipher = new AesCbcPkcs5FcImpl(key, iv);

        fileCipher.encrypt(dataFile, encDataFile);

        fileCipher.decrypt(encDataFile, decDataFile);


    }
}
