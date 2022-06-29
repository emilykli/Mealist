package com.example.mealist.GroceryList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealist.AddRecipe.Ingredient;
import com.example.mealist.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private Context mContext;
    private List<Ingredient> mIngredients;


    protected ListAdapter(Context context, List<Ingredient> ingredients) {
        mContext = context;
        mIngredients = ingredients;
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
    class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox mCbListItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCbListItem = itemView.findViewById(R.id.cbListItem);
        }

        public void bind(Ingredient ingredient) {
                ingredient.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        mCbListItem.setText(ingredient.getName());
                    }
                });
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
