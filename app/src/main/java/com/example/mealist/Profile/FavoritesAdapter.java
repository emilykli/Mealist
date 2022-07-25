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

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesViewHolder>{

    private Context mContext;
    private List<Recipe> mFavorites;

    public FavoritesAdapter(Context context, List<Recipe> favorites) {
        mContext = context;
        mFavorites = favorites;
    }

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_favorites, parent, false);
        return new FavoritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder holder, int position) {
        Recipe favorite = mFavorites.get(position);
        holder.itemView.setTag(favorite);
        holder.bind(favorite);
    }

    @Override
    public int getItemCount() {
        return mFavorites.size();
    }

    public void clear() {
        mFavorites.clear();
        notifyDataSetChanged();
    }
}
