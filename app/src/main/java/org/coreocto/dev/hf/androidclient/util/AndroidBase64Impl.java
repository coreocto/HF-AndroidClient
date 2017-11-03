package org.coreocto.dev.hf.androidclient.util;

import android.util.Base64;
import org.coreocto.dev.hf.commonlib.util.IBase64;

public class AndroidBase64Impl implements IBase64 {
    @Override
    public String encodeToString(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    @Override
    public byte[] decodeToByteArray(String s) {
        return Base64.decode(s, Base64.NO_WRAP);
    }

}
