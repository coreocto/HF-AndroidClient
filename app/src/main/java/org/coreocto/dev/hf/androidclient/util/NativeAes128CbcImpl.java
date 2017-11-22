package org.coreocto.dev.hf.androidclient.util;

import org.coreocto.dev.hf.commonlib.crypto.IBlockCipherCbc;

public class NativeAes128CbcImpl implements IBlockCipherCbc {

    static {
        System.loadLibrary("native-lib");
    }

    public native byte[] aesCbcEncrypt(byte[] key, byte[] data);

    public native byte[] aesCbcDecrypt(byte[] key, byte[] data);

    @Override
    public byte[] encrypt(byte[] iv, byte[] key, byte[] data) {
        if (
//                iv == null ||
                key == null || data == null) {
            return null;
        } else {
            return aesCbcEncrypt(key, data);
        }
    }

    @Override
    public byte[] decrypt(byte[] iv, byte[] key, byte[] data) {
        if (
//                iv == null ||
                        key == null || data == null) {
            return null;
        } else {
            return aesCbcDecrypt(key, data);
        }
    }
}
