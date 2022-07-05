package com.example.mealist.GroceryList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealist.AddRecipe.Ingredient;
import com.example.mealist.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private Context mContext;
    private List<Ingredient> mIngredients;

    private List<Integer> mCheckedIngredientPositions = new ArrayList<>();


    protected ListAdapter(Context context, List<Ingredient> ingredients) {
        mContext = context;
        mIngredients = ingredients;
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        holder.mCbListItem.setOnCheckedChangeListener(null);
        super.onViewRecycled(holder);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient ingredient = mIngredients.get(position);
        holder.itemView.setTag(ingredient);
        holder.bind(ingredient);

    }

    @Override
    public int getItemCount() {
        return mIngredients.size();
    }

    // TODO: add onclicklistener
    class ViewHolder extends RecyclerView.ViewHolder implements CheckBox.OnCheckedChangeListener{

        private CheckBox mCbListItem;
        private TextView mTvListItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCbListItem = itemView.findViewById(R.id.cbListItem);
            mTvListItem = itemView.findViewById(R.id.tvListItem);
        }

        public void bind(Ingredient ingredient) {
            long start = System.currentTimeMillis();
                ingredient.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        mTvListItem.setText(ingredient.getName());
                        long time_elapsed = System.currentTimeMillis() - start;
                        Log.i("time", "time for each bind: " + time_elapsed);
                    }
                });
        }


        @Override
        public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
            Log.i("checked", mCheckedIngredientPositions.toString());
            if (checkBox.isChecked()) {
                final Ingredient ingredient = (Ingredient) checkBox.getTag();
                int index = mIngredients.indexOf(ingredient);
                mCheckedIngredientPositions.add(index);
                Log.i("checked", index + "");
            }
        }
    }

    public void clear() {
        mIngredients.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Ingredient> list) {
        long startAdding = System.currentTimeMillis();
        mIngredients.addAll(list);
        notifyDataSetChanged();
        Log.i("time", "adding to adapter thing: " + (System.currentTimeMillis() - startAdding));
    }

    public void addIngredient(Ingredient ingredient) {
        mIngredients.add(ingredient);
        notifyItemInserted(mIngredients.size() - 1);
    }
}
