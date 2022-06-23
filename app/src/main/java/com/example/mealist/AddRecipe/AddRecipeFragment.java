package com.example.mealist.AddRecipe;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.mealist.R;
import com.example.mealist.Backend.SpoonacularClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;

public class AddRecipeFragment extends Fragment {
    public static final String TAG = "AddRecipeFragment";

    private TextView mTvMealName;
    private String mMealName;

    private EditText mEtRecipeSearch;
    private Button mBtnSearch;

    SpoonacularClient client;

    public AddRecipeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMealName = getArguments().getString("meal", "default meal");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        client = new SpoonacularClient();

        mTvMealName = view.findViewById(R.id.tvMealName);
        mTvMealName.setText(mMealName);

        mEtRecipeSearch = view.findViewById(R.id.etRecipeSearch);
        mBtnSearch = view.findViewById(R.id.btnSearch);
        setupSearchButton();
    }

    public void setupSearchButton() {
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = mEtRecipeSearch.getText().toString();
                if (searchQuery.isEmpty()) {
                    Toast.makeText(getContext(), "search cannot be empty", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "search is empty");
                    return;
                }
                client.getRecipes(searchQuery, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        JSONObject jsonObject = json.jsonObject;
                        try {
                            JSONArray results = jsonObject.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject recipe = (JSONObject) results.get(i);
                                Log.i(TAG, recipe.toString());
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "jsonException", e);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "search failed", throwable);
                    }
                });
                mEtRecipeSearch.setText("");

            }
        });
    }
}