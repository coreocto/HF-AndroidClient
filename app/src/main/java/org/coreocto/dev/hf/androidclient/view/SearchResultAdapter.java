package org.coreocto.dev.hf.androidclient.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.coreocto.dev.hf.androidclient.R;
import org.coreocto.dev.hf.androidclient.bean.FileInfo;

import java.util.List;

public class SearchResultAdapter extends ArrayAdapter<FileInfo> {

    public SearchResultAdapter(Context context, List<FileInfo> items) {
        super(context, R.layout.search_result_list_item, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View view = super.getView(position, convertView, parent);

        String itemTxt = getItem(position).getName();

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.search_result_list_item, parent, false);
        }

        TextView txt1 = (TextView) convertView.findViewById(android.R.id.text1);

        txt1.setText(itemTxt);

        return convertView;
    }
}
