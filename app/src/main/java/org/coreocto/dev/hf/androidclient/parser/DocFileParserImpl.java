package org.coreocto.dev.hf.androidclient.parser;

import android.util.Log;
import org.coreocto.dev.hf.clientlib.LibConstants;
import org.coreocto.dev.hf.clientlib.parser.IFileParser;
import org.textmining.text.extraction.WordExtractor;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DocFileParserImpl implements IFileParser {

    private static final String TAG = "DocFileParserImpl";

    @Override
    public List<String> getText(File file) {

        List<String> result = null;

        try {
            result = getText(new BufferedInputStream(new FileInputStream(file)));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "error when reading content from file", e);
        }

        return result;
    }

    @Override
    public List<String> getText(InputStream inputStream) {
        List<String> result = new ArrayList<>();

        String text = null;
        try {
            WordExtractor extractor = new WordExtractor();
            //进行提取对doc文件
            text = extractor.extractText(inputStream);
            text = text.toLowerCase();
            result.addAll(Arrays.asList(text.split(LibConstants.REGEX_SPLIT_CHARS)));
        } catch (Exception e) {
            Log.e(TAG, "error when reading content from file", e);
        }

        return result;
    }
}
