package org.coreocto.dev.hf.androidclient.util;

import org.coreocto.dev.hf.commonlib.util.IMd5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AndroidMd5Impl implements IMd5 {

    private MessageDigest md5 = null;

    {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] getHash(String s) {
        if (s == null) {
            return null;
        } else {
            return getHash(s.getBytes());
        }
    }

    @Override
    public byte[] getHash(byte[] bytes) {

        if (bytes == null) {
            return null;
        }

        return md5.digest(bytes);
    }
}
