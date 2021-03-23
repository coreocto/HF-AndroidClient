package org.coreocto.dev.hf.androidclient.wrapper;

import org.coreocto.dev.hf.clientlib.parser.IFileParser;
import org.coreocto.dev.hf.clientlib.sse.vasst.VasstClient;
import org.coreocto.dev.hf.commonlib.crypto.IByteCipher;
import org.coreocto.dev.hf.commonlib.crypto.IFileCipher;
import org.coreocto.dev.hf.commonlib.sse.vasst.bean.TermFreq;
import org.coreocto.dev.hf.commonlib.util.IBase64;
import org.coreocto.dev.hf.perfmon.annotation.PrefMon;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public class VasstClientW extends VasstClient {
    public VasstClientW(IBase64 base64) {
        super(base64);
    }

    @Override
    public byte[] getSecretKey() {
        return super.getSecretKey();
    }

    @Override
    public void setSecretKey(byte[] secretKey) {
        super.setSecretKey(secretKey);
    }

    @Override
    public void GenKey(int noOfBytes) {
        super.GenKey(noOfBytes);
    }

    @PrefMon
    public TermFreq Preprocessing(InputStream inputStream, BigDecimal x, boolean includePrefix, boolean includeSuffix, IFileParser fileParser, IByteCipher byteCipher, Map<String, String> addInfo) throws BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, IOException, IllegalBlockSizeException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        TermFreq result = super.Preprocessing(inputStream, x, includePrefix, includeSuffix, fileParser, byteCipher);
        addInfo.put("wordCount", result.getTerms().size() + "");
        return result;
    }

    @PrefMon
    public TermFreq Preprocessing(File inFile, BigDecimal x, boolean includePrefix, boolean includeSuffix, IFileParser fileParser, IByteCipher byteCipher, Map<String, String> addInfo) throws BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IOException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException {
        TermFreq result = super.Preprocessing(inFile, x, includePrefix, includeSuffix, fileParser, byteCipher);
        addInfo.put("wordCount", result.getTerms().size() + "");
        return result;
    }

    @PrefMon
    public List<String> CreateReq(String query, BigDecimal x, IByteCipher byteCipher, Map<String, String> addInfo) throws BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, IOException, IllegalBlockSizeException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        return super.CreateReq(query, x, byteCipher);
    }

    @PrefMon
    public void Encrypt(File fi, File fo, IFileCipher fileCipher, Map<String, String> addInfo) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IOException {
        super.Encrypt(fi, fo, fileCipher);
    }

    @PrefMon
    public void Decrypt(File fi, File fo, IFileCipher fileCipher, Map<String, String> addInfo) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IOException {
        super.Decrypt(fi, fo, fileCipher);
    }

    @PrefMon
    public void Encrypt(InputStream fis, OutputStream fos, IFileCipher fileCipher, Map<String, String> addInfo) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
        super.Encrypt(fis, fos, fileCipher);
    }

    @PrefMon
    public void Decrypt(InputStream fis, OutputStream fos, IFileCipher fileCipher, Map<String, String> addInfo) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
        super.Decrypt(fis, fos, fileCipher);
    }
}
