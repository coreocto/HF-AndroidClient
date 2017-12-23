package org.coreocto.dev.hf.androidclient.parser;

import android.util.Log;
import org.coreocto.dev.hf.clientlib.Constants;
import org.coreocto.dev.hf.clientlib.parser.IFileParser;
import org.textmining.text.extraction.WordExtractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DocFileParserImpl implements IFileParser {

    private static final String TAG = "DocFileParserImpl";

    @Override
    public List<String> getText(File file) {

        List<String> result = new ArrayList<>();

        //创建输入流用来读取doc文件
        FileInputStream in = null;
        String text = null;
        try {
            in = new FileInputStream(file);
            WordExtractor extractor = new WordExtractor();
            //进行提取对doc文件
            text = extractor.extractText(in);
            text = text.toLowerCase();
            result.addAll(Arrays.asList(text.split(Constants.SPACE)));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
            }
        }

        return result;
    }
}
