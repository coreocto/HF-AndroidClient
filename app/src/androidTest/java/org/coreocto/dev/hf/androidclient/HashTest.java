package org.coreocto.dev.hf.androidclient;

import android.os.Debug;
import android.util.Base64;
import android.util.Log;
import org.coreocto.dev.hf.commonlib.crypto.BlockCipherFactory;
import org.coreocto.dev.hf.commonlib.crypto.HashFuncFactory;
import org.junit.Test;

import javax.crypto.Cipher;
import java.security.MessageDigest;
import java.util.zip.CRC32;

public class HashTest {

    private static final String TAG = "HashTest";

    //@Test
    public void useAppContext() throws Exception {

    }

    //@Test
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
            MessageDigest md = HashFuncFactory.getMessageDigest("MD5");
            byte[] result = md.digest(data);
            Log.v(TAG, Base64.encodeToString(result, Base64.NO_WRAP));
            long end = Debug.threadCpuTimeNanos() - start;
            Log.d(TAG, "MD5, runtime = " + end + "ms");
        }

        {
            long start = Debug.threadCpuTimeNanos();
            MessageDigest md = HashFuncFactory.getMessageDigest("SHA-1");
            byte[] result = md.digest(data);
            Log.v(TAG, Base64.encodeToString(result, Base64.NO_WRAP));
            long end = Debug.threadCpuTimeNanos() - start;
            Log.d(TAG, "SHA-1 runtime = " + end + "ms");
        }

        {
            long start = Debug.threadCpuTimeNanos();
            MessageDigest md = HashFuncFactory.getMessageDigest("SHA-256");
            byte[] result = md.digest(data);
            Log.v(TAG, Base64.encodeToString(result, Base64.NO_WRAP));
            long end = Debug.threadCpuTimeNanos() - start;
            Log.d(TAG, "SHA-256 runtime = " + end + "ms");
        }

        {
            long start = Debug.threadCpuTimeNanos();
            MessageDigest md = HashFuncFactory.getMessageDigest("SHA-512");
            byte[] result = md.digest(data);
            Log.v(TAG, Base64.encodeToString(result, Base64.NO_WRAP));
            long end = Debug.threadCpuTimeNanos() - start;
            Log.d(TAG, "SHA-512 runtime = " + end + "ms");
        }

//        {
//            long start = Debug.threadCpuTimeNanos();
//            CRC32 crc32 = new CRC32();
//            crc32.update(data);
//            Log.v(TAG, "CRC32 = "+crc32.getValue());
//            long end = Debug.threadCpuTimeNanos() - start;
//            Log.d(TAG, "CRC32, runtime = " + Math.round(end / 1000000) + "ms");
//        }
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
