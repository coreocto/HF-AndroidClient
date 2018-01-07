package org.coreocto.dev.hf.androidclient.wrapper;

import org.coreocto.dev.hf.clientlib.parser.IFileParser;
import org.coreocto.dev.hf.clientlib.sse.suise.SuiseClient;
import org.coreocto.dev.hf.commonlib.crypto.IByteCipher;
import org.coreocto.dev.hf.commonlib.crypto.IFileCipher;
import org.coreocto.dev.hf.commonlib.sse.suise.bean.AddTokenResult;
import org.coreocto.dev.hf.commonlib.sse.suise.bean.SearchTokenResult;
import org.coreocto.dev.hf.commonlib.sse.suise.util.SuiseUtil;
import org.coreocto.dev.hf.commonlib.util.Registry;
import org.coreocto.dev.hf.perfmon.annotation.DebugTrace;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class SuiseClientW extends SuiseClient {
    public SuiseClientW(Registry registry, SuiseUtil suiseUtil) {
        super(registry, suiseUtil);
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
    @DebugTrace
    public void Gen(int noOfBytes) {
        super.Gen(noOfBytes);
    }

    // the following method should not be invoked
    @Override
    public void Dec(InputStream fis, OutputStream fos, IFileCipher fileCipher) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        throw new UnsupportedOperationException("");
    }

    @DebugTrace
    public void Dec(InputStream fis, OutputStream fos, IFileCipher fileCipher, Map<String, String> addInfo) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
        super.Dec(fis, fos, fileCipher);
    }

    // the following method should not be invoked
    @Override
    public void Enc(InputStream fis, OutputStream fos, IFileCipher fileCipher) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        throw new UnsupportedOperationException("");
    }

    @DebugTrace
    public void Enc(InputStream fis, OutputStream fos, IFileCipher fileCipher, Map<String, String> addInfo) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
        super.Enc(fis, fos, fileCipher);
    }

    // the following method should not be invoked
    @Override
    public void Enc(File fi, File fo, IFileCipher fileCipher) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException {
        throw new UnsupportedOperationException("");
    }

    @DebugTrace
    public void Enc(File fi, File fo, IFileCipher fileCipher, Map<String, String> addInfo) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IOException {
        super.Enc(fi, fo, fileCipher);
    }

    // the following method should not be invoked
    @Override
    public void Dec(File fi, File fo, IFileCipher fileCipher) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException {
        throw new UnsupportedOperationException("");
    }

    @DebugTrace
    public void Dec(File fi, File fo, IFileCipher fileCipher, Map<String, String> addInfo) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IOException {
        super.Dec(fi, fo, fileCipher);
    }

    // the following method should not be invoked
    @Override
    public AddTokenResult AddToken(InputStream inputStream, boolean includeSubStr, String docId, IFileParser fileParser, IByteCipher byteCipher) throws BadPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        return super.AddToken(inputStream, includeSubStr, docId, fileParser, byteCipher);
    }

    @DebugTrace
    public AddTokenResult AddToken(InputStream inputStream, boolean includeSubStr, String docId, IFileParser fileParser, IByteCipher byteCipher, Map<String, String> addInfo) throws BadPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        AddTokenResult result = super.AddToken(inputStream, includeSubStr, docId, fileParser, byteCipher);
        addInfo.put("wordCount", result.getC().size() + "");
        return result;
    }

    // the following method should not be invoked
    @Override
    public AddTokenResult AddToken(File inFile, boolean includeSubStr, String docId, IFileParser fileParser, IByteCipher byteCipher) throws BadPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, FileNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        throw new UnsupportedOperationException("");
    }

    @DebugTrace
    public AddTokenResult AddToken(File inFile, boolean includeSubStr, String docId, IFileParser fileParser, IByteCipher byteCipher, Map<String, String> addInfo) throws BadPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, FileNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        return super.AddToken(inFile, includeSubStr, docId, fileParser, byteCipher);
    }

    // the following method should not be invoked
    @Override
    public SearchTokenResult SearchToken(String keyword, IByteCipher byteCipher) throws BadPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        throw new UnsupportedOperationException("");
    }

    @DebugTrace
    public SearchTokenResult SearchToken(String keyword, IByteCipher byteCipher, Map<String, String> addInfo) throws BadPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        return super.SearchToken(keyword, byteCipher);
    }
}
