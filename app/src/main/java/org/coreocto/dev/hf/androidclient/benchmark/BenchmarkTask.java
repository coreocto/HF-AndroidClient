package org.coreocto.dev.hf.androidclient.benchmark;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.coreocto.dev.hf.androidclient.Constants;
import org.coreocto.dev.hf.androidclient.activity.NavDwrActivity;
import org.coreocto.dev.hf.androidclient.fragment.cryptotest.ChartResultFragment;
import org.coreocto.dev.hf.commonlib.crypto.BlockCipherFactory;
import org.coreocto.dev.hf.commonlib.crypto.HashFuncFactory;

import javax.crypto.Cipher;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class BenchmarkTask extends AsyncTask<BenchmarkParam, BenchmarkResult, List<BenchmarkResult>> {

    private final String TAG = this.getClass().toString();
    private Context mContext;
    private ProgressDialog mDialog;

    public BenchmarkTask(Context mContext) {
        this.mContext = mContext;
    }

    private static String getUniqueId() {
        return UUID.randomUUID().toString();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //updateText("Benchmarking...");

        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage("Benchmarking...");
        mDialog.setCancelable(false);
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.show();
    }

    //@Profiling(showParamValues = true, timeType = MeasureType.DEBUG_THREADCPUTIMENANOS)
    private long getProcessTime(String type, byte[] data, int runCnt, int dataSize/*, boolean explicitGc*/) {
        long tsLong = System.currentTimeMillis();

        if (type != null && (type.startsWith(BlockCipherFactory.CIPHER_AES) || type.startsWith(BlockCipherFactory.CIPHER_DES) || type.startsWith(BlockCipherFactory.CIPHER_DESede))) {

            Cipher encCipher = null;

            int keySize = -1;
            int ivSize = -1;

            if (type.startsWith(BlockCipherFactory.CIPHER_AES)
//                    || type.startsWith(BlockCipherFactory.CIPHER_DESede)
 ) {
                keySize = 16;
                ivSize = 16;
            } else {
                ivSize = 8;
                keySize = type.startsWith(BlockCipherFactory.CIPHER_DESede) ? 16:8;
            }

            try {

                if (type.contains(BlockCipherFactory.SEP + BlockCipherFactory.MODE_CBC + BlockCipherFactory.SEP)) {

                    Log.d(TAG, type);

                    encCipher = BlockCipherFactory.getCipher(type.substring(0, 3),
                            type,
                            Cipher.ENCRYPT_MODE,
                            new byte[keySize],
                            new byte[ivSize]
                    );

                } else {
                    encCipher = BlockCipherFactory.getCipher(type.substring(0, 3),
                            type,
                            Cipher.ENCRYPT_MODE,
                            new byte[keySize]
                    );
                }

                encCipher.doFinal(data);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            MessageDigest digest = null;
            try {
                digest = HashFuncFactory.getMessageDigest(type);
                digest.digest(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        if (HashUtil.MD_SHA_1.equals(type)) {
//            for (int i = runCnt; i > 0; i--) {
//                try {
//                    HashUtil.getSha1sum(RandomUtil.getRandomBytes(dataSize));
//                } catch (NoSuchAlgorithmException e) {
//                    Log.e(getClass().getCanonicalName(), e.getMessage());
//                    //e.printStackTrace();
//                }
//            }
//        } else if (HashUtil.MD_MD5.equals(type)) {
//            for (int i = runCnt; i > 0; i--) {
//                try {
//                    HashUtil.getMd5sum(RandomUtil.getRandomBytes(dataSize));
//                } catch (NoSuchAlgorithmException e) {
//                    Log.e(getClass().getCanonicalName(), e.getMessage());
//                    //e.printStackTrace();
//                }
//            }
//        } else if (EncryptUtil.AES_CBC_PKCS5.equals(type)) {
//
//            Cipher bc = BlockCipherFactory.
//
//            BlockCipher bc = new BlockCipherImpl(EncryptUtil.AES_CBC_PKCS5);
//
//            for (int i = runCnt; i > 0; i--) {
//                try {
//                    bc.encrypt(key, DEFAULT_16B, RandomUtil.getRandomBytes(dataSize));
//                } catch (Exception e) {
//                    Log.e(getClass().getCanonicalName(), e.getMessage());
//                }
//            }
//        } else if (EncryptUtil.AES_ECB_PKCS5.equals(type)) {
//
//            BlockCipher bc = new BlockCipherImpl(EncryptUtil.AES_ECB_PKCS5);
//
//            for (int i = runCnt; i > 0; i--) {
//                try {
//                    bc.encrypt(key, RandomUtil.getRandomBytes(dataSize));
//                } catch (Exception e) {
//                    Log.e(getClass().getCanonicalName(), e.getMessage());
//                }
//            }
//        } else if (EncryptUtil.DES_CBC_PKCS5.equals(type)) {
//
//            BlockCipher bc = new BlockCipherImpl(EncryptUtil.DES_CBC_PKCS5);
//
//            for (int i = runCnt; i > 0; i--) {
//                try {
//                    bc.encrypt(key, DEFAULT_8B, RandomUtil.getRandomBytes(dataSize));
//                } catch (Exception e) {
//                    Log.e(getClass().getCanonicalName(), e.getMessage());
//                }
//            }
//        } else if (EncryptUtil.DES_ECB_PKCS5.equals(type)) {
//
//            BlockCipher bc = new BlockCipherImpl(EncryptUtil.DES_ECB_PKCS5);
//
//            for (int i = runCnt; i > 0; i--) {
//                try {
//                    bc.encrypt(key, RandomUtil.getRandomBytes(dataSize));
//                } catch (Exception e) {
//                    Log.e(getClass().getCanonicalName(), e.getMessage());
//                }
//            }
//        } else if (EncryptUtil.DESede_CBC_PKCS5.equals(type)) {
//
//            BlockCipher bc = new BlockCipherImpl(EncryptUtil.DESede_CBC_PKCS5);
//
//            for (int i = runCnt; i > 0; i--) {
//                try {
//                    bc.encrypt(key, RandomUtil.getRandomBytes(dataSize));
//                } catch (Exception e) {
//                    Log.e(getClass().getCanonicalName(), e.getMessage());
//                }
//            }
//        } else if (EncryptUtil.DESede_ECB_PKCS5.equals(type)) {
//
//            BlockCipher bc = new BlockCipherImpl(EncryptUtil.DESede_ECB_PKCS5);
//
//            for (int i = runCnt; i > 0; i--) {
//                try {
//                    bc.encrypt(key, RandomUtil.getRandomBytes(dataSize));
//                } catch (Exception e) {
//                    Log.e(getClass().getCanonicalName(), e.getMessage());
//                }
//            }
//        }
        long ttLong = System.currentTimeMillis() - tsLong;

//        if (explicitGc) {
//            MemoryUtil.freeMemory();
//        }
//
        return ttLong;
    }
//
//    private long getProcessTime(String type, List<byte[]> rawDataList, byte[] key, boolean explicitGc) {
//        long tsLong = Debug.threadCpuTimeNanos();
//        if (CryptoTestContent.MD_SHA_1.equals(type)) {
//            for (byte[] data : rawDataList) {
//                try {
//                    HashUtil.getSha1sum(data);
//                } catch (NoSuchAlgorithmException e) {
//                    Log.e(getClass().getCanonicalName(), e.getMessage());
//                }
//            }
//        } else if (CryptoTestContent.MD_MD5.equals(type)) {
//            IMd5 md5 = new AndroidMd5Impl();
//            for (byte[] data : rawDataList) {
//                md5.getHash(data);
//            }
//        } else if (CryptoTestContent.AES_CBC_PKCS5.equals(type)) {
//
//            BlockCipher bc = new BlockCipherImpl(EncryptUtil.AES_CBC_PKCS5);
//
//            for (byte[] data : rawDataList) {
//                try {
//                    bc.encrypt(key, DEFAULT_16B, data);
//                } catch (Exception e) {
//                    Log.e(getClass().getCanonicalName(), e.getMessage());
//                }
//            }
//        } else if (CryptoTestContent.AES_ECB_PKCS5.equals(type)) {
//
//            BlockCipher bc = new BlockCipherImpl(EncryptUtil.AES_ECB_PKCS5);
//
//            for (byte[] data : rawDataList) {
//                try {
//                    bc.encrypt(key, data);
//                } catch (Exception e) {
//                    Log.e(getClass().getCanonicalName(), e.getMessage());
//                }
//            }
//        } else if (CryptoTestContent.DES_CBC_PKCS5.equals(type)) {
//
//            BlockCipher bc = new BlockCipherImpl(EncryptUtil.DES_CBC_PKCS5);
//
//            for (byte[] data : rawDataList) {
//                try {
//                    bc.encrypt(key, DEFAULT_8B, data);
//                } catch (Exception e) {
//                    Log.e(getClass().getCanonicalName(), e.getMessage());
//                }
//            }
//        } else if (CryptoTestContent.DES_ECB_PKCS5.equals(type)) {
//
//            BlockCipher bc = new BlockCipherImpl(EncryptUtil.DES_ECB_PKCS5);
//
//            for (byte[] data : rawDataList) {
//                try {
//                    bc.encrypt(key, data);
//                } catch (Exception e) {
//                    Log.e(getClass().getCanonicalName(), e.getMessage());
//                }
//            }
//        } else if (CryptoTestContent.DESede_CBC_PKCS5.equals(type)) {
//
//            BlockCipher bc = new BlockCipherImpl(EncryptUtil.DESede_CBC_PKCS5);
//
//            for (byte[] data : rawDataList) {
//                try {
//                    bc.encrypt(key, data);
//                } catch (Exception e) {
//                    Log.e(getClass().getCanonicalName(), e.getMessage());
//                }
//            }
//        } else if (CryptoTestContent.DESede_ECB_PKCS5.equals(type)) {
//
//            BlockCipher bc = new BlockCipherImpl(EncryptUtil.DESede_ECB_PKCS5);
//
//            for (byte[] data : rawDataList) {
//                try {
//                    bc.encrypt(key, data);
//                } catch (Exception e) {
//                    Log.e(getClass().getCanonicalName(), e.getMessage());
//                }
//            }
//        }
//        long ttLong = Debug.threadCpuTimeNanos() - tsLong;
//
//        if (explicitGc) {
//            MemoryUtil.freeMemory();
//        }
//
//        return ttLong;
//    }

    @Override
    protected List<BenchmarkResult> doInBackground(BenchmarkParam... params) {

        BenchmarkParam param = params[0];
        mDialog.setMax(param.getTestCnt());

        String batchId = getUniqueId();

//        List<byte[]> rawDataList = null;

//        if (param.isAllocMem()) {
//            //prepare raw data
//            rawDataList = new ArrayList<byte[]>();
//            int dataSize = param.getDataSize();
//            for (int i = param.getRunCnt(); i > 0; i--) {
//                rawDataList.add(RandomUtil.getRandomBytes(dataSize));
//            }
//            //end prepare raw data
//        }

        int dataSize = param.getDataSize();
        byte[] data = new byte[dataSize];

        BenchmarkResult[] results = new BenchmarkResult[param.getTestCnt()];

        List<BenchmarkResult> resultList = new ArrayList<>();

        int idx = 0;

//        BenchmarkResultDAO dao = new BenchmarkResultDAO(mContext);
//
        Iterator<String> iter = param.getTestIds().iterator();

        while (iter.hasNext()) {
            String testId = iter.next();

            long ttLong = -1;

//            if (param.isAllocMem()) {
//                if (testId.startsWith("AES") || testId.startsWith("DESede")) {
//                    ttLong = getProcessTime(testId, rawDataList, DEFAULT_16B, param.isExplicitGc());
//                } else if (testId.startsWith("DES")) {
//                    ttLong = getProcessTime(testId, rawDataList, DEFAULT_8B, param.isExplicitGc());
//                } else {
//                    ttLong = getProcessTime(testId, rawDataList, null, param.isExplicitGc());
//                }
//            } else {
            if (testId.startsWith("AES") || testId.startsWith("DESede")) {
                ttLong = getProcessTime(testId, data, param.getTestCnt(), param.getDataSize()/*, param.isExplicitGc()*/);
            } else if (testId.startsWith("DES")) {
                ttLong = getProcessTime(testId, data, param.getTestCnt(), param.getDataSize()/*, param.isExplicitGc()*/);
            } else {
                ttLong = getProcessTime(testId, data, param.getTestCnt(), param.getDataSize()/*, param.isExplicitGc()*/);
            }
//            }
            int roundNumber = (int) ttLong; //Math.round(ttLong / 1000000);

            results[idx] = new BenchmarkResult();
            results[idx].setTime(roundNumber);
            results[idx].setType(testId);
            results[idx].setBatchId(batchId);
            results[idx].setRunCnt(param.getRunCnt());
            results[idx].setDataSize(param.getDataSize());

//            dao.insert(results[idx]);

            resultList.add(results[idx]);

            publishProgress(results);
            idx++;
        }

        return resultList;
    }

    @Override
    protected void onProgressUpdate(BenchmarkResult... results) {
        int finishCnt = 0;
        for (int i = results.length - 1; i >= 0; i--) {
            if (results[i] != null) {
                finishCnt++;
            }
        }
        mDialog.setProgress(finishCnt);
    }

    @Override
    protected void onPostExecute(List<BenchmarkResult> resultList) {

        mDialog.dismiss();
        if (mContext instanceof NavDwrActivity) {
            ChartResultFragment chartResultFrag = (ChartResultFragment) ((NavDwrActivity) mContext).goToFragment(Constants.FRAGMENT_CHART_RESULT, true);
            chartResultFrag.setBarChartData(resultList);
        }
    }
}
