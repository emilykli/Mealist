package com.example.mealist.GroceryList;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealist.R;

public class AisleHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    final View mRootView;
    final TextView mTvAisle;
    final CardView mCvAisle;

    public AisleHeaderViewHolder(@NonNull View itemView) {
        super(itemView);

        mRootView = itemView;
        mTvAisle = itemView.findViewById(R.id.tvAisle);
        mCvAisle = itemView.findViewById(R.id.cvAisle);

        mCvAisle.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        AisleSection section = (AisleSection) v.getTag();
        ListFragment.headerClicked(section);
    }
}
