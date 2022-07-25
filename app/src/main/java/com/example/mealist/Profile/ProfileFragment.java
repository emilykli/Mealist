package com.example.mealist.Profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mealist.Access.User;
import com.example.mealist.AddRecipe.Recipe;
import com.example.mealist.MakeMealPlan.MealPlan;
import com.example.mealist.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {

    private TextView mTvUsername;

    private RecyclerView mRvFavorites;
    private FavoritesAdapter mFavoritesAdapter;
    private List<Recipe> mFavorites;

    private RecyclerView mRvRecents;
    private RecentsAdapter mRecentsAdapter;
    private List<Recipe> mRecents;

    private LinearLayoutManager mVerticalLinearLayoutManager = new LinearLayoutManager(getContext(),
            LinearLayoutManager.VERTICAL, false);
    private LinearLayoutManager mHorizontalLinearLayoutManager = new LinearLayoutManager(getContext(),
            LinearLayoutManager.HORIZONTAL, false);

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTvUsername = view.findViewById(R.id.tvUsername);
        mTvUsername.setText("hello " + ParseUser.getCurrentUser().getUsername() + "!");

        mRecents = new ArrayList<>();
        setupRecents(view);

        setupFavorites(view);

    }

    private void setupRecents(View view) {
        ParseQuery<MealPlan> recentsQuery = ParseQuery.getQuery(MealPlan.class);
        recentsQuery.setLimit(3);
        recentsQuery.whereEqualTo(MealPlan.KEY_OWNER, ParseUser.getCurrentUser());
        recentsQuery.addDescendingOrder("createdAt");
        recentsQuery.findInBackground(new FindCallback<MealPlan>() {
            @Override
            public void done(List<MealPlan> plans, ParseException e) {
                for (MealPlan plan : plans) {
                    mRecents.addAll(plan.getList(MealPlan.KEY_BREAKFAST));
                    mRecents.addAll(plan.getList(MealPlan.KEY_LUNCH));
                    mRecents.addAll(plan.getList(MealPlan.KEY_DINNER));
                }
                mRvRecents = view.findViewById(R.id.rvRecents);
                mRecentsAdapter = new RecentsAdapter(getContext(), mRecents);
                mRvRecents.setAdapter(mRecentsAdapter);
                mRvRecents.setLayoutManager(mHorizontalLinearLayoutManager);
            }
        });
    }

    private void setupFavorites(View view) {
        mRvFavorites = view.findViewById(R.id.rvFavorites);
        mFavorites = ParseUser.getCurrentUser().getList(User.KEY_FAVORITES);
        mFavoritesAdapter = new FavoritesAdapter(getContext(), mFavorites);
        mRvFavorites.setAdapter(mFavoritesAdapter);
        mRvFavorites.setLayoutManager(mVerticalLinearLayoutManager);
    }
}