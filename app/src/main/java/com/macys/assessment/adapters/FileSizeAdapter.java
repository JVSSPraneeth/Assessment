package com.macys.assessment.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;

import com.macys.assessment.R;
import com.macys.assessment.models.util.KeyValueEntry;

import java.util.List;

/**
 * Created by pjatapro on 9/21/16.
 * <p>
 * Recycler-view Adapter for displaying Largest Files list.
 *
 * @extends RecyclerView.Adapter
 */
@SuppressWarnings("ALL")
public class FileSizeAdapter extends RecyclerView.Adapter<RecyclerRowViewHolder> {

    // Adapter Data-state caching.
    private Context mContext;
    private List<KeyValueEntry> mFileResultsList;

    /**
     * Default Constructor caching Context and Key-Value Pair Entries.
     *
     * @param context
     * @param fileResultsList
     */
    public FileSizeAdapter(Context context, List<KeyValueEntry> fileResultsList) {
        mContext = context;
        mFileResultsList = fileResultsList;
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
        KeyValueEntry result = getItem(position);
        String fileName = "File Name - " + result.getKey();
        String fileSize = "File Size - " + Formatter.formatFileSize(mContext, result.getValue());

        holder.mTopText.setText(fileName);
        holder.mBottomText.setText(fileSize);
    }

    /**
     * Recycler-view Adapter Callback to retrieve Item-count of cached-items.
     *
     * @return itemCount
     */
    @Override
    public int getItemCount() {
        return mFileResultsList.size();
    }

    /**
     * Recycler-view Adapter Callback to retrieve Item from the cached-item based off
     * position.
     *
     * @param position
     * @return cached-item
     */
    public KeyValueEntry getItem(int position) {
        return mFileResultsList.get(position);
    }
}
