package com.example.mealist.Profile;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealist.AddRecipe.Recipe;
import com.example.mealist.R;

public class FavoritesViewHolder extends RecyclerView.ViewHolder {

    final TextView mTvFavorite;


    public FavoritesViewHolder(@NonNull View itemView) {
        super(itemView);

        mTvFavorite = itemView.findViewById(R.id.tvFavorite);

    }

    public void bind(Recipe recipe) {
        recipe.fetchIfNeededInBackground((object, e) -> mTvFavorite.setText(object.getString(Recipe.KEY_NAME)));
        mTvFavorite.setMovementMethod(new ScrollingMovementMethod());
    }

}
