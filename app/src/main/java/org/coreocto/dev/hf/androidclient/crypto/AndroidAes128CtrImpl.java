package org.coreocto.dev.hf.androidclient.crypto;

import org.coreocto.dev.hf.commonlib.crypto.BlockCipherFactory;
import org.coreocto.dev.hf.commonlib.crypto.IBlockCipherCbc;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class AndroidAes128CtrImpl implements IBlockCipherCbc {

    private Cipher encCipher = null;
    private Cipher decCipher = null;

    private static final byte[] DEFAULT_IV = new byte[16];
    public static final String CIPHER_TRANSFORMATION = "AES/CTR/PKCS5Padding";
    public static final String CIPHER = "AES";

    public byte[] encrypt(byte[] iv, byte[] key, byte[] data) {

        if (iv == null || key == null || data == null) {
            return null;
        }

        if (encCipher == null) {
            try {
                encCipher = BlockCipherFactory.getCipher(BlockCipherFactory.CIPHER_AES,
                        BlockCipherFactory.CIPHER_AES + BlockCipherFactory.SEP + BlockCipherFactory.MODE_CBC + BlockCipherFactory.SEP + BlockCipherFactory.PADDING_PKCS5,
                        Cipher.ENCRYPT_MODE,
                        key,
                        iv);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        byte[] result = null;

        try {
            result = encCipher.doFinal(data);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return result;
    }


    public byte[] decrypt(byte[] iv, byte[] key, byte[] data) {
        if (iv == null || key == null || data == null) {
            return null;
        }

        if (decCipher == null) {
            try {
                decCipher = BlockCipherFactory.getCipher(BlockCipherFactory.CIPHER_AES,
                        BlockCipherFactory.CIPHER_AES + BlockCipherFactory.SEP + BlockCipherFactory.MODE_CBC + BlockCipherFactory.SEP + BlockCipherFactory.PADDING_PKCS5,
                        Cipher.DECRYPT_MODE,
                        key,
                        iv);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        byte[] result = null;

        try {
            result = decCipher.doFinal(data);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return result;
    }
}
