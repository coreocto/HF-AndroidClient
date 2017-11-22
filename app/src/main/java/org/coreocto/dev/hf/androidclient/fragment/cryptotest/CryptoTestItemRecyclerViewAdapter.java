package org.coreocto.dev.hf.androidclient.fragment.cryptotest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.coreocto.dev.hf.androidclient.R;

import java.util.ArrayList;
import java.util.List;

public class CryptoTestItemRecyclerViewAdapter extends RecyclerView.Adapter<CryptoTestItemRecyclerViewAdapter.ViewHolder> {
    public final String TAG = this.getClass().toString();

    private final List<CryptoTestItem> mValues;
    private final CryptoTestItemFragment.OnListFragmentInteractionListener mListener;

    public CryptoTestItemRecyclerViewAdapter(List<CryptoTestItem> items, CryptoTestItemFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_cryptotestitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.mNameView.setText(holder.mItem.getSchemeName());
        holder.mEnabledView.setChecked(true);
        holder.mProgressView.setVisibility(View.INVISIBLE);

        holder.mEnabledView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                holder.mItem.setEnabled(holder.mEnabledView.isChecked());
            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                holder.mItem.setEnabled(!holder.mItem.isEnabled());
                holder.mEnabledView.setChecked(holder.mItem.isEnabled());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public int getCheckedCount() {
        int i = 0;
        for (CryptoTestItem item : mValues) {
            if (item.isEnabled()) {
                i++;
            }
        }
        return i;
    }

    public List<CryptoTestItem> getCheckedItems() {
        List<CryptoTestItem> results = new ArrayList<>();
        for (CryptoTestItem item : mValues) {
            if (item.isEnabled()) {
                results.add(item);
            }
        }
        return results;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public final TextView mNameView;
        public final CheckBox mEnabledView;
        public final ProgressBar mProgressView;

        public CryptoTestItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mNameView = (TextView) view.findViewById(R.id.name);
            mEnabledView = (CheckBox) view.findViewById(R.id.enabled);
            mProgressView = (ProgressBar) view.findViewById(R.id.progress);
        }
    }
}
