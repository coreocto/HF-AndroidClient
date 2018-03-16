package org.coreocto.dev.hf.androidclient.wrapper;

import org.coreocto.dev.hf.clientlib.parser.IFileParser;
import org.coreocto.dev.hf.clientlib.sse.suise.SuiseClient;
import org.coreocto.dev.hf.commonlib.crypto.IFileCipher;
import org.coreocto.dev.hf.commonlib.crypto.IKeyedHashFunc;
import org.coreocto.dev.hf.commonlib.sse.suise.bean.AddTokenResult;
import org.coreocto.dev.hf.commonlib.sse.suise.bean.SearchTokenResult;
import org.coreocto.dev.hf.commonlib.sse.suise.util.SuiseUtil;
import org.coreocto.dev.hf.commonlib.util.IBase64;
import org.coreocto.dev.hf.perfmon.annotation.PrefMon;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Random;

public class SuiseClientW extends SuiseClient {

    public SuiseClientW(SuiseUtil suiseUtil, IBase64 base64) {
        super(suiseUtil, base64);
    }

    @Override
    public byte[] getKey1() {
        return super.getKey1();
    }

    @Override
    public byte[] getKey2() {
        return super.getKey2();
    }

    @Override
    @PrefMon
    public void Gen(int noOfBytes) {
        super.Gen(noOfBytes);
    }

    @PrefMon
    public void Dec(InputStream fis, OutputStream fos, IFileCipher fileCipher, Map<String, String> addInfo) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
        super.Dec(fis, fos, fileCipher);
    }

    @PrefMon
    public void Enc(InputStream fis, OutputStream fos, IFileCipher fileCipher, Map<String, String> addInfo) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
        super.Enc(fis, fos, fileCipher);
    }

    @PrefMon
    public void Enc(File fi, File fo, IFileCipher fileCipher, Map<String, String> addInfo) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IOException {
        super.Enc(fi, fo, fileCipher);
    }

    @PrefMon
    public void Dec(File fi, File fo, IFileCipher fileCipher, Map<String, String> addInfo) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IOException {
        super.Dec(fi, fo, fileCipher);
    }

    @PrefMon
    public AddTokenResult AddToken(InputStream inputStream, boolean includePrefix, boolean includeSuffix, String docId, IFileParser fileParser, IKeyedHashFunc keyedHashFunc, Random random, Map<String, String> addInfo) throws BadPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        AddTokenResult result = super.AddToken(inputStream, includePrefix, includeSuffix, docId, fileParser, keyedHashFunc, random);
        addInfo.put("wordCount", result.getC().size() + "");
        return result;
    }

    @PrefMon
    public AddTokenResult AddToken(File inFile, boolean includePrefix, boolean includeSuffix, String docId, IFileParser fileParser, IKeyedHashFunc keyedHashFunc, Random random, Map<String, String> addInfo) throws BadPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, FileNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        return super.AddToken(inFile, includePrefix, includeSuffix, docId, fileParser, keyedHashFunc, random);
    }

    @PrefMon
    public SearchTokenResult SearchToken(String keyword, IKeyedHashFunc keyedHashFunc, Map<String, String> addInfo) throws BadPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        return super.SearchToken(keyword, keyedHashFunc);
    }
}
