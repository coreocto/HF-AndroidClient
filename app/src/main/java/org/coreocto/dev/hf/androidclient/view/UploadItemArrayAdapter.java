package org.coreocto.dev.hf.androidclient.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.coreocto.dev.hf.androidclient.R;
import org.coreocto.dev.hf.androidclient.bean.UploadItem;

import java.util.List;

public class UploadItemArrayAdapter extends ArrayAdapter<UploadItem> {

    public UploadItemArrayAdapter(Context context, List<UploadItem> items) {
        super(context, R.layout.upload_queue_list_item, R.id.tvUri, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = super.getView(position, convertView, parent);

        UploadItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.upload_queue_list_item, parent, false);
        }

        TextView tvUri = (TextView) convertView.findViewById(R.id.tvUri);

        tvUri.setText(item.getUri().toString());

        TextView tvStatus = (TextView) convertView.findViewById(R.id.tvStatus);

        tvStatus.setText(item.getStatus().toString());

        switch(item.getStatus()){
            case ERROR:
                tvStatus.setTextColor(Color.RED);
                break;
            case PENDING:
                tvStatus.setTextColor(Color.BLACK);
                break;
            case FINISHED:
                tvStatus.setTextColor(Color.GREEN);
                break;
        }

        return convertView;

//        // ...
//        // Lookup view for data population
//        Button btButton = (Button) convertView.findViewById(R.id.btButton);
//        // Cache row position inside the button using `setTag`
//        btButton.setTag(position);
//        // Attach the click event handler
//        btButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int position = (Integer) view.getTag();
//                // Access the row position here to get the correct data item
//                Uri user = getItem(position);
//                // Do what you want here...
//            }
//        });
//        // ... other view population as needed...
//        // Return the completed view
//        return convertView;
    }
}
