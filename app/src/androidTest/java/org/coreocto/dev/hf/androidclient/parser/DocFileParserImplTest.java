package org.coreocto.dev.hf.androidclient.parser;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.util.Log;
import org.coreocto.dev.hf.androidclient.Constants;
import org.coreocto.dev.hf.androidclient.db.DatabaseHelper;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.UUID;

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
        for (String s:text){
            Log.d(TAG, s);
        }
    }
}
