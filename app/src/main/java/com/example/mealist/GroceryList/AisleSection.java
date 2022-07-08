package com.example.mealist.GroceryList;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealist.AddRecipe.Ingredient;
import com.example.mealist.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class AisleSection extends Section{

    private final String mTitle;
    private final List<Ingredient> mIngredients;

    private boolean mExpanded = true;

    AisleSection(@NonNull final String title, @NonNull final List<Ingredient> ingredients) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_list_ingredient)
                .headerResourceId(R.layout.item_aisle)
                .build());
        mTitle = title;
        mIngredients = ingredients;
    }

    @Override
    public int getContentItemsTotal() {
        return mExpanded ? mIngredients.size() : 0;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        final IngredientViewHolder ingredientHolder = (IngredientViewHolder) holder;

        final Ingredient ingredient = mIngredients.get(position);

        ingredientHolder.mCbListItem.setTag(ingredient);

        ingredient.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                ingredientHolder.mTvListItem.setText(ingredient.getName());
                if (ingredient.getChecked()) {
                    ingredientHolder.mCbListItem.setChecked(true);
                }
                else {
                    ingredientHolder.mCbListItem.setChecked(false);
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(final View view) {
        return new AisleHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(final RecyclerView.ViewHolder holder) {
        final AisleHeaderViewHolder headerHolder = (AisleHeaderViewHolder) holder;

        headerHolder.mTvAisle.setText(mTitle);
        headerHolder.mCvAisle.setTag(this);

    }

    boolean isExpanded() {
        return mExpanded;
    }

    void setExpanded(final boolean expanded) {
        mExpanded = expanded;
    }

}
