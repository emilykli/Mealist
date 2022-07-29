package com.example.mealist.Profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.mealist.AddRecipe.Recipe;
import com.example.mealist.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

public class RecentsViewHolder extends RecyclerView.ViewHolder {

    final View mRootView;
    final Context mContext;
    final ImageView mIvRecentRecipeImage;
    final TextView mTvRecentRecipeName;

    public RecentsViewHolder(@NonNull View itemView, Context context) {
        super(itemView);

        mRootView = itemView;
        mContext = context;
        mIvRecentRecipeImage = itemView.findViewById(R.id.ivRecentRecipeImage);
        mTvRecentRecipeName = itemView.findViewById(R.id.tvRecentRecipeName);

    }

    public void bind(Recipe recipe) {
        recipe.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                String name = object.getString(Recipe.KEY_NAME);
                String imageUrl = object.getString(Recipe.KEY_IMAGE_LINK);

                mTvRecentRecipeName.setText(name);

                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(mContext).load(imageUrl)
                            .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(10)))
                            .into(mIvRecentRecipeImage);

                }

                else {
                    Glide.with(mContext).load(R.mipmap.appicon)
                            .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(10)))
                            .into(mIvRecentRecipeImage);
                }
            }
        });
    }


}
