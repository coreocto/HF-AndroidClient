package org.coreocto.dev.hf.androidclient.parser;

import android.util.Log;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.coreocto.dev.hf.androidclient.Constants;
import org.coreocto.dev.hf.clientlib.parser.IFileParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DocFileParserImpl implements IFileParser {

    private static final String TAG = "DocFileParserImpl";

    @Override
    public List<String> getText(File file) {

        List<String> result = new ArrayList<>();

        XWPFDocument doc = null;
        try {
            doc = new XWPFDocument(new FileInputStream(file));
            for (XWPFParagraph paragraph : doc.getParagraphs()) {
                String pageText = paragraph.getText();
                pageText = pageText.toLowerCase();
                result.addAll(Arrays.asList(pageText.split(Constants.SPACE)));
//                String content = StringUtils.abbreviate(paragraph.getText(), 20);
//                if (StringUtils.isEmpty(content)) {
//                    content = "<empty>";
//                }
            }
        } catch (IOException e) {
            Log.e(TAG, "error when reading content from file", e);
        } finally {
            try {
                doc.close();
            } catch (IOException e) {
            }
        }
        return result;
    }
}
