package org.coreocto.dev.hf.androidclient.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import org.coreocto.dev.hf.androidclient.Constants;
import org.coreocto.dev.hf.androidclient.db.DatabaseHelper;
import org.coreocto.dev.hf.androidclient.util.ByteArrayWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {
    private static final String TAG = "DatabaseHelperTest";

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        DatabaseHelper databaseHelper = new DatabaseHelper(appContext, Constants.LOCAL_APP_DB, null, 1);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        String guid = UUID.randomUUID().toString();

        //clear all records before test
        //test insert record
        {
            ContentValues values = new ContentValues();
            values.put("cremotename", guid);
            long rowId = database.insert(Constants.TABLE_REMOTE_DOCS, null, values);
            Log.d(TAG, "rowId(insert): " + rowId);
        }

        //test update record
        {
            ContentValues values = new ContentValues();
            values.put("cremoteid", "hello");
            long affectedRows = database.update(Constants.TABLE_REMOTE_DOCS, values, "cremotename=?", new String[]{guid});
            Log.d(TAG, "affectedRows(update): " + affectedRows);
        }
    }
}
