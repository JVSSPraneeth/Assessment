package com.macys.assessment.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.macys.assessment.R;

/**
 * Created by pjatapro on 9/21/16.
 * <p>
 * View-Holder pattern for RecyclerViews displaying Scan-results.
 *
 * @extends RecyclerView.ViewHolder
 */
@SuppressWarnings("ALL")
public class RecyclerRowViewHolder extends RecyclerView.ViewHolder {

    /**
     * Display-view elements reference caching.
     */
    public TextView mTopText = (TextView) itemView.findViewById(R.id.recycler_top_text);
    public TextView mBottomText = (TextView) itemView.findViewById(R.id.recycler_bottom_text);

    /**
     * Default Constructor to encapsulate underlying display-view.
     *
     * @param itemView
     */
    public RecyclerRowViewHolder(View itemView) {
        super(itemView);
    }
}
