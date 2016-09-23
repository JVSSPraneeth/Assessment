package com.macys.assessment.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.macys.assessment.R;
import com.macys.assessment.models.util.KeyValueEntry;

import java.util.List;

/**
 * Created by pjatapro on 9/21/16.
 * <p>
 * RecyclerView-Adapter for File Extension Type Count display.
 *
 * @extends RecyclerView.Adapter
 */
@SuppressWarnings("ALL")
public class FileTypeCountAdapter extends RecyclerView.Adapter<RecyclerRowViewHolder> {

    // Adapter Data-state caching.
    private Context mContext;
    private List<KeyValueEntry> mDataMap;

    /**
     * Default Constructor caching Context and Key-Value Pair Entries.
     *
     * @param context
     * @param fileResultsList
     */
    public FileTypeCountAdapter(Context context, List<KeyValueEntry> fileTypeCountMap) {
        mContext = context;
        mDataMap = fileTypeCountMap;
    }

    /**
     * Recycler-view Adapter Callback inorder to create and cache View-Holder.
     *
     * @param parent
     * @param viewType
     * @return view-holder
     */
    @Override
    public RecyclerRowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = ViewGroup.inflate(mContext, R.layout.recycler_row, null);
        return new RecyclerRowViewHolder(rowView);
    }

    /**
     * Recycler-view Adapter Callback inorder to re-use and re-bind View-Holder
     * to Display-view.
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerRowViewHolder holder, int position) {
        KeyValueEntry entry = getItem(position);
        String key = "File Type - ." + entry.getKey();
        String value = "Recurrence count - " + String.valueOf(entry.getValue());
        holder.mTopText.setText(key);
        holder.mBottomText.setText(value);
    }

    /**
     * Recycler-view Adapter Callback to retrieve Item-count of cached-items.
     *
     * @return itemCount
     */
    @Override
    public int getItemCount() {
        return mDataMap.size();
    }

    /**
     * Recycler-view Adapter Callback to retrieve Item from the cached-item based off
     * position.
     *
     * @param position
     * @return cached-item
     */
    public KeyValueEntry getItem(int position) {
        return mDataMap.get(position);
    }
}
