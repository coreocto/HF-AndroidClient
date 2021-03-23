package org.coreocto.dev.hf.androidclient.wrapper;

import org.coreocto.dev.hf.clientlib.parser.IFileParser;
import org.coreocto.dev.hf.clientlib.sse.chlh.Chlh2Client;
import org.coreocto.dev.hf.commonlib.crypto.IByteCipher;
import org.coreocto.dev.hf.commonlib.sse.chlh.Index;
import org.coreocto.dev.hf.commonlib.util.IBase64;
import org.coreocto.dev.hf.perfmon.annotation.PrefMon;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public class Chlh2ClientW extends Chlh2Client {

    public Chlh2ClientW(IBase64 base64, double falsePositiveProbability, int expectedNumberOfElements) {
        super(base64, falsePositiveProbability, expectedNumberOfElements);
    }

    @PrefMon
    public Index BuildIndex(InputStream is, IFileParser fileParser, String docId, IByteCipher byteCipher, Map<String, String> addInfo) throws BadPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        Index output = super.BuildIndex(is, fileParser, docId, byteCipher);
        addInfo.put("wordCount", output.getWordCnt() + "");
        addInfo.put("fpr", new BigDecimal(output.getFalsePositive()).toPlainString());
        return output;
    }

    @Override
    public List<String> Trapdoor(String w) {
        return super.Trapdoor(w);
    }

}
