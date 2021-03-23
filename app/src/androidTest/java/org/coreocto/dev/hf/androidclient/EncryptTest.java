package org.coreocto.dev.hf.androidclient;

import android.os.Debug;
import android.support.test.runner.AndroidJUnit4;
import android.util.Base64;
import android.util.Log;
import org.coreocto.dev.hf.commonlib.crypto.BlockCipherFactory;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.crypto.Cipher;

@RunWith(AndroidJUnit4.class)
public class EncryptTest {

    private static final String TAG = "EncryptTest";

    //@Test
    public void useAppContext() throws Exception {

    }

    @Test
    public void oneKilo() throws Exception {
        byte[] oneKilo = new byte[1024];

        Log.d(TAG, "oneKilo");

        calComputeTime(oneKilo);
    }

    @Test
    public void fiveKilo() throws Exception {
        byte[] oneKilo = new byte[5120];

        Log.d(TAG, "fiveKilo");

        calComputeTime(oneKilo);
    }

    @Test
    public void tenKilo() throws Exception {
        byte[] oneKilo = new byte[10240];
        Log.d(TAG, "tenKilo");

        calComputeTime(oneKilo);
    }

    private void calComputeTime(byte[] data) throws Exception {

        byte[] iv = new byte[16];
        byte[] key = new byte[16];

        byte[] ivDes = new byte[8];
        byte[] keyDes = new byte[8];
        byte[] keyDes3 = new byte[24];

        {
            long start = Debug.threadCpuTimeNanos();
            Cipher aesCipher = BlockCipherFactory.getCipher("AES", "AES/CBC/PKCS5Padding", Cipher.ENCRYPT_MODE, key, iv);
            byte[] result = aesCipher.doFinal(data);
            Log.v(TAG, Base64.encodeToString(result, Base64.NO_WRAP));
            long end = Debug.threadCpuTimeNanos() - start;
            Log.d(TAG, "AES/CBC, runtime = " + Math.round(end / 1000000) + "ms");
        }

        {
            long start = Debug.threadCpuTimeNanos();
            Cipher aesCipher = BlockCipherFactory.getCipher("AES", "AES/ECB/PKCS5Padding", Cipher.ENCRYPT_MODE, key);
            byte[] result = aesCipher.doFinal(data);
            Log.v(TAG, Base64.encodeToString(result, Base64.NO_WRAP));
            long end = Debug.threadCpuTimeNanos() - start;
            Log.d(TAG, "AES/ECB, runtime = " + Math.round(end / 1000000) + "ms");
        }

        {
            long start = Debug.threadCpuTimeNanos();
            Cipher aesCipher = BlockCipherFactory.getCipher("DES", "DES/CBC/PKCS5Padding", Cipher.ENCRYPT_MODE, keyDes, ivDes);
            byte[] result = aesCipher.doFinal(data);
            Log.v(TAG, Base64.encodeToString(result, Base64.NO_WRAP));
            long end = Debug.threadCpuTimeNanos() - start;
            Log.d(TAG, "DES/CBC, runtime = " + Math.round(end / 1000000) + "ms");
        }

        {

            long start = Debug.threadCpuTimeNanos();
            Cipher aesCipher = BlockCipherFactory.getCipher("DES", "DES/ECB/PKCS5Padding", Cipher.ENCRYPT_MODE, keyDes);
            byte[] result = aesCipher.doFinal(data);
            Log.v(TAG, Base64.encodeToString(result, Base64.NO_WRAP));
            long end = Debug.threadCpuTimeNanos() - start;
            Log.d(TAG, "DES/ECB, runtime = " + Math.round(end / 1000000) + "ms");
        }

        {
            long start = Debug.threadCpuTimeNanos();
            Cipher aesCipher = BlockCipherFactory.getCipher("DESede", "DESede/CBC/PKCS5Padding", Cipher.ENCRYPT_MODE, key, ivDes);
            byte[] result = aesCipher.doFinal(data);
            Log.v(TAG, Base64.encodeToString(result, Base64.NO_WRAP));
            long end = Debug.threadCpuTimeNanos() - start;
            Log.d(TAG, "DESede/CBC, runtime = " + Math.round(end / 1000000) + "ms");
        }

        {
            long start = Debug.threadCpuTimeNanos();
            Cipher aesCipher = BlockCipherFactory.getCipher("DESede", "DESede/ECB/PKCS5Padding", Cipher.ENCRYPT_MODE, key);
            byte[] result = aesCipher.doFinal(data);
            Log.v(TAG, Base64.encodeToString(result, Base64.NO_WRAP));
            long end = Debug.threadCpuTimeNanos() - start;
            Log.d(TAG, "DESede/ECB, runtime = " + Math.round(end / 1000000) + "ms");
        }
    }

    @Test
    public void fiftyKilo() throws Exception {
        byte[] oneKilo = new byte[51200];

        Log.d(TAG, "fiftyKilo");

        calComputeTime(oneKilo);
    }

    @Test
    public void hundredKilo() throws Exception {
        byte[] oneKilo = new byte[102400];


        Log.d(TAG, "hundredKilo");

        calComputeTime(oneKilo);
    }

    @Test
    public void fiveHundredKilo() throws Exception {
        byte[] oneKilo = new byte[512000];

        Log.d(TAG, "fiveHundredKilo");

        calComputeTime(oneKilo);
    }
}
