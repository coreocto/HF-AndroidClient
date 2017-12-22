package org.coreocto.dev.hf.androidclient.parser;

import android.util.Log;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.coreocto.dev.hf.androidclient.Constants;
import org.coreocto.dev.hf.clientlib.parser.IFileParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PdfFileParserImpl implements IFileParser {

    private static final String TAG = "PdfFileParserImpl";

    @Override
    public List<String> getText(File file) {
        List<String> result = new ArrayList<>();

        PdfReader in = null;

        try {
            in = new PdfReader(new FileInputStream(file));

            int pageCnt = in.getNumberOfPages();

            for (int i = 1; i <= pageCnt; i++) {
                String pageText = PdfTextExtractor.getTextFromPage(in, i);
                pageText = pageText.toLowerCase();
                result.addAll(Arrays.asList(pageText.split(Constants.SPACE)));
            }
        } catch (IOException e) {
            Log.e(TAG, "error when reading content from file", e);
        } finally {
            in.close();
        }

        return result;
    }
}
