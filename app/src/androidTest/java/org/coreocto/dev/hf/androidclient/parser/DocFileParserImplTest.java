package org.coreocto.dev.hf.androidclient.parser;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.util.Log;
import org.coreocto.dev.hf.clientlib.LibConstants;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class DocFileParserImplTest {
    private static final String TAG = "DocFileParserImplTest";

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        File extStor = Environment.getExternalStorageDirectory();
        File wordDoc = new File(extStor, "SampleDOCFile_100kb.doc");

        DocFileParserImpl fileParser = new DocFileParserImpl();
        List<String> text = fileParser.getText(wordDoc);

        StringBuilder content = new StringBuilder();

        for (String s : text) {
//            Log.d(TAG, s);
            content.append(s + " ");
        }

        String[] result = content.toString().split(LibConstants.REGEX_SPLIT_CHARS);
        for (String s : result) {
            Log.d(TAG, s);
        }

    }
}
