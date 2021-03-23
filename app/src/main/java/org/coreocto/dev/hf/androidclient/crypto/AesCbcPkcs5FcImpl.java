package org.coreocto.dev.hf.androidclient.crypto;

import org.coreocto.dev.hf.commonlib.crypto.BlockCipherFactory;
import org.coreocto.dev.hf.commonlib.crypto.IFileCipher;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AesCbcPkcs5FcImpl implements IFileCipher {
    private static final String CIPHER_AES = "AES";
    private static final String CIPHER_TRANSFORM = "AES/CBC/PKCS5Padding";

    private byte[] key = null;
    private byte[] iv = null;

    public AesCbcPkcs5FcImpl(byte[] key, byte[] iv) {
        this.key = key;
        this.iv = iv;
    }

    @Override
    public void encrypt(File in, File out) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IOException {
        this.encrypt(new FileInputStream(in), new FileOutputStream(out));
    }

    @Override
    public void encrypt(InputStream in, OutputStream out) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IOException {
        Cipher encryptCipher = BlockCipherFactory.getCipher(CIPHER_AES, CIPHER_TRANSFORM, Cipher.ENCRYPT_MODE, key, iv);

        BufferedInputStream is = new BufferedInputStream(in);
        BufferedOutputStream os = new BufferedOutputStream(new CipherOutputStream(out, encryptCipher));
        int buffer = -1;
        while ((buffer = is.read()) != -1) {
            os.write(buffer);
        }
        os.flush();

        if (is != null) {
            is.close();
        }
        if (os != null) {
            os.close();
        }
    }

    @Override
    public void decrypt(File in, File out) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        this.decrypt(new FileInputStream(in), new FileOutputStream(out));
    }

    @Override
    public void decrypt(InputStream in, OutputStream out) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IOException {
        Cipher decryptCipher = BlockCipherFactory.getCipher(CIPHER_AES, CIPHER_TRANSFORM, Cipher.DECRYPT_MODE, key, iv);

        BufferedInputStream is = new BufferedInputStream(new CipherInputStream(in, decryptCipher));
        BufferedOutputStream os = new BufferedOutputStream(out);
        int buffer = -1;
        while ((buffer = is.read()) != -1) {
            os.write(buffer);
        }
        os.flush();

        if (is != null) {
            is.close();
        }

        if (os != null) {
            os.close();
        }
    }
}
