package com.example.mealist.AddRecipe;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mealist.R;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private Context mContext;
    private List<Recipe> mRecipes;
    private FragmentManager mFragmentManager;

    public RecipeAdapter(Context context, List<Recipe> recipes, FragmentManager fragmentManager) {
        mContext = context;
        mRecipes = recipes;
        mFragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recipe_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.ViewHolder holder, int position) {
        Recipe recipe = mRecipes.get(position);
        holder.itemView.setTag(recipe);
        holder.bind(recipe);
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    public void clear() {
        mRecipes.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView mIvRecipeImage;
        private TextView mTvRecipeName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvRecipeImage = itemView.findViewById(R.id.ivRecipeImage);
            mTvRecipeName = itemView.findViewById(R.id.tvRecipeName);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            final Recipe recipe = (Recipe) v.getTag();
            if (recipe != null) {
                Intent i = new Intent(mContext, RecipeDetailActivity.class);
                i.putExtra("recipe", recipe);
                mContext.startActivity(i);
            }
        }

        public void bind(Recipe recipe) {
            mTvRecipeName.setText(recipe.getName());
            String imageLink = recipe.getImageLink();

            if (!imageLink.isEmpty()) {
                Glide.with(mContext).load(imageLink).into(mIvRecipeImage);
            }
        }

    }
}
