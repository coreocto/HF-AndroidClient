package org.coreocto.dev.hf.androidclient.crypto;

import org.coreocto.dev.hf.commonlib.crypto.BlockCipherFactory;
import org.coreocto.dev.hf.commonlib.crypto.IByteCipher;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AesCtrPkcs5BcImpl implements IByteCipher {
    private static final String CIPHER_AES = "AES";
    private static final String CIPHER_TRANSFORM = "AES/CTR/PKCS5Padding";

    private byte[] key = null;
    private byte[] iv = null;

    public AesCtrPkcs5BcImpl(byte[] key, byte[] iv) {
        this.key = key;
        this.iv = iv;
    }

    public AesCtrPkcs5BcImpl(){

    }

    @Override
    public byte[] encrypt(byte[] data) throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException {
        Cipher encryptCipher = BlockCipherFactory.getCipher(CIPHER_AES, CIPHER_TRANSFORM, Cipher.ENCRYPT_MODE, key, iv);
        return encryptCipher.doFinal(data);
    }

    @Override
    public byte[] decrypt(byte[] data) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        Cipher decryptCipher = BlockCipherFactory.getCipher(CIPHER_AES, CIPHER_TRANSFORM, Cipher.DECRYPT_MODE, key, iv);
        return decryptCipher.doFinal(data);
    }

    @Override
    public byte[] encrypt(byte[] data, byte[] keyBytes, byte[] ivBytes) throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException {
        Cipher encryptCipher = BlockCipherFactory.getCipher(CIPHER_AES, CIPHER_TRANSFORM, Cipher.ENCRYPT_MODE, keyBytes, ivBytes);
        return encryptCipher.doFinal(data);
    }

    @Override
    public byte[] decrypt(byte[] data, byte[] keyBytes, byte[] ivBytes) throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException {
        Cipher decryptCipher = BlockCipherFactory.getCipher(CIPHER_AES, CIPHER_TRANSFORM, Cipher.DECRYPT_MODE, keyBytes, ivBytes);
        return decryptCipher.doFinal(data);
    }

    @Override
    public byte[] encrypt(byte[] bytes, byte[] bytes1) throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] decrypt(byte[] bytes, byte[] bytes1) throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException {
        throw new UnsupportedOperationException();
    }
}
