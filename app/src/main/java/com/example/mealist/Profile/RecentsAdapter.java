package com.example.mealist.Profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealist.AddRecipe.Recipe;
import com.example.mealist.R;

import java.util.List;

public class RecentsAdapter extends RecyclerView.Adapter<RecentsViewHolder>{

    private Context mContext;
    private List<Recipe> mRecents;

    public RecentsAdapter(Context context, List<Recipe> recents) {
        mContext = context;
        mRecents = recents;
    }

    @NonNull
    @Override
    public RecentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recent, parent, false);
        return new RecentsViewHolder(view, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentsViewHolder holder, int position) {
        Recipe recent = mRecents.get(position);
        holder.itemView.setTag(recent);
        holder.bind(recent);
    }

    @Override
    public int getItemCount() {
        return mRecents.size();
    }

    public void clear() {
        mRecents.clear();
        notifyDataSetChanged();
    }
}
