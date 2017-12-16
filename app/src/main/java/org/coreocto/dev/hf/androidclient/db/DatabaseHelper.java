package org.coreocto.dev.hf.androidclient.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.coreocto.dev.hf.androidclient.Constants;

public class DatabaseHelper extends SQLiteOpenHelper {

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + Constants.TABLE_REMOTE_DOCS +
                "(_id integer primary key not null," +
                "cremoteid varchar," +
                "cremotename varchar)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public DatabaseHelper(Context context, String name,
                          SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
}
