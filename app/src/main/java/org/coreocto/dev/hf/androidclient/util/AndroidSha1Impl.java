package org.coreocto.dev.hf.androidclient.util;

import org.coreocto.dev.hf.commonlib.crypto.IHashFunc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AndroidSha1Impl implements IHashFunc {
    private MessageDigest sha1 = null;

    {
        try {
            sha1 = MessageDigest.getInstance("MD5");
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

        return sha1.digest(bytes);
    }
}
