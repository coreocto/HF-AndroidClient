package org.coreocto.dev.hf.androidclient.view;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import org.coreocto.dev.hf.androidclient.AppConstants;
import org.coreocto.dev.hf.androidclient.bean.AppSettings;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<String> {

    private static final String TAG = "AutoCompleteAdapter";

    private Filter mFilter;

    private List<String> mSubData = new ArrayList<String>();
    static int counter = 0;

    public AutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        setNotifyOnChange(false);

        final AppSettings appSettings = AppSettings.getInstance();

        mFilter = new Filter() {
            private int c = ++counter;
            private List<String> mData = new ArrayList<String>();

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // This method is called in a worker thread
                mData.clear();

                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    SQLiteDatabase database = null;
                    Cursor c = null;
                    try {

                        database = appSettings.getDatabaseHelper().getReadableDatabase();
                        c = database.rawQuery("select ckeyword from " + AppConstants.TABLE_AUTO_COMPLETE + " where ckeyword like ? order by _id desc limit ?", new String[]{constraint + "%", "5"});

                        while (c.moveToNext()) {
                            mData.add(c.getString(c.getColumnIndex("ckeyword")));
                        }

//                        Collection<String> history = appSettings.getSuiseClient().getSearchHistory();
//                        mData.addAll(history);

                    } catch (Exception e) {
                        Log.e(TAG, "error when fetching last keywords", e);
                    }

                    if (c != null) {
                        c.close();
                    }

                    if (database != null) {
                        database.close();
                    }

                    filterResults.values = mData;
                    filterResults.count = mData.size();
                }
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence contraint, FilterResults results) {
                if (c == counter) {
                    mSubData.clear();
                    if (results != null && results.count > 0) {
                        ArrayList<String> objects = (ArrayList<String>) results.values;
                        for (String v : objects) {
                            mSubData.add(v);
                        }

                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            }
        };
    }

    @Override
    public int getCount() {
        return mSubData.size();
    }

    @Override
    public String getItem(int index) {
        return mSubData.get(index);
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }
}
