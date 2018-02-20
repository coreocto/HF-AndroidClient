package org.coreocto.dev.hf.androidclient.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.File;

public class FileUtil {
    public static long getFileSize(Context ctx, Uri docUri) {
        long result = -1;
        if (docUri.getScheme() != null && docUri.getScheme().startsWith("content")) {
            Cursor returnCursor = ctx.getContentResolver().query(docUri, null, null, null, null);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            result = returnCursor.getLong(sizeIndex);
            returnCursor.close();
        } else if (docUri.getScheme() != null && docUri.getScheme().startsWith("file")) {
            File fileRef = new File(docUri.getPath());
            result = fileRef.length();
        }
        return result;
    }
}
