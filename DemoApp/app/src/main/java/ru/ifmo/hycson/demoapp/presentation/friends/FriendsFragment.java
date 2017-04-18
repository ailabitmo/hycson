package ru.ifmo.hycson.demoapp.presentation.friends;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

import java.util.List;

import ru.ifmo.hycson.demoapp.App;
import ru.ifmo.hycson.demoapp.R;
import ru.ifmo.hycson.demoapp.presentation.profile.ProfileFragment;
import ru.ifmo.hycson.demoapp.presentation.profile.entities.ProfileData;

public class FriendsFragment extends MvpFragment<FriendsContract.View, FriendsContract.Presenter>
        implements FriendsContract.View, FriendsAdapter.OnFriendClickListener {

    private static final String BUNDLE_FRIENDS_URL = "ru.ifmo.hycson.demoapp.presentation.friends.url";

    private View mProgressView;
    private FriendsAdapter mAdapter;

    public static Fragment newInstance(String friendsUrl) {
        Fragment instance = new FriendsFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_FRIENDS_URL, friendsUrl);
        instance.setArguments(args);
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragments_friends, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.fragment_friends_title));

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter = new FriendsAdapter(this));

        mProgressView = view.findViewById(R.id.progressView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getPresenter().loadFriendsData(getArguments().getString(BUNDLE_FRIENDS_URL));
    }

    @Override
    public void onFriendClick(ProfileData profileData) {
        Fragment profileFragment = ProfileFragment.newInstance(profileData.getId());
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, profileFragment)
                .addToBackStack(null)
                .commit();
    }

    @NonNull
    @Override
    public FriendsContract.Presenter createPresenter() {
        return App.getApp(getContext()).plusHympComponent().plusFriendsComponent().presenter();
    }

    @Override
    public void showLoading() {
        mProgressView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressView.setVisibility(View.GONE);
    }

    @Override
    public void showError(Throwable e) {
        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setFriends(List<ProfileData> friends) {
        mAdapter.addFriends(friends);
    }
}
