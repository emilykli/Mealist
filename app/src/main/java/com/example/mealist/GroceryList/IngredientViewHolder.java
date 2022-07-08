package com.example.mealist.GroceryList;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealist.AddRecipe.Ingredient;
import com.example.mealist.R;

public class IngredientViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {

    final View mRootView;
    final CheckBox mCbListItem;
    final TextView mTvListItem;

    public IngredientViewHolder(@NonNull View view) {
        super(view);

        mRootView = view;
        mCbListItem = view.findViewById(R.id.cbListItem);
        mTvListItem = view.findViewById(R.id.tvListItem);
        mCbListItem.setOnCheckedChangeListener(this);

    }

    @Override
    public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
        Ingredient ingredient = (Ingredient) checkBox.getTag();
        if(checkBox.isChecked()) {
            ingredient.setChecked(true);
        }
        else {
            ingredient.setChecked(false);
        }
        ingredient.saveInBackground();
    }


}
