package org.coreocto.dev.hf.androidclient.wrapper;

import org.coreocto.dev.hf.clientlib.sse.mces.McesClient;
import org.coreocto.dev.hf.commonlib.util.IBase64;

public class McesClientW extends McesClient{

    public McesClientW(IBase64 base64) {
        super(base64);
    }
}
