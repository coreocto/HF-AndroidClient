package org.coreocto.dev.hf.androidclient.util;

import org.coreocto.dev.hf.commonlib.util.IAes128Cbc;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AndroidAes128CbcImpl implements IAes128Cbc {

    private Cipher encCipher = null;
    private Cipher decCipher = null;

    private static final byte[] DEFAULT_IV = new byte[16];
    public static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    public static final String CIPHER = "AES";

    @Override
    public byte[] encrypt(byte[] iv, byte[] key, byte[] data) {

        if (iv == null || key == null || data == null) {
            return null;
        }

        if (encCipher == null) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, CIPHER);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            try {
                encCipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
                encCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
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

    @Override
    public byte[] decrypt(byte[] iv, byte[] key, byte[] data) {
        if (iv == null || key == null || data == null) {
            return null;
        }

        if (decCipher == null) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, CIPHER);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            try {
                decCipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
                decCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
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
