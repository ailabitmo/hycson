package ru.ifmo.hycson.demoapp.presentation.friends;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.ifmo.hycson.demoapp.R;
import ru.ifmo.hycson.demoapp.presentation.profile.entities.ProfileData;
import ru.ifmo.hycson.demoapp.util.CircleTransformation;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ProfileViewHolder> {
    private final List<ProfileData> mFriends;
    private final OnFriendClickListener mOnFriendClickListener;

    interface OnFriendClickListener {
        void onFriendClick(ProfileData profileData);
    }

    public FriendsAdapter(OnFriendClickListener onFriendClickListener) {
        mFriends = new ArrayList<>();
        mOnFriendClickListener = onFriendClickListener;
    }

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_person_profile, parent, false);
        final ProfileViewHolder viewHolder = new ProfileViewHolder(itemView);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileData profileData = mFriends.get(viewHolder.getAdapterPosition());
                if (mOnFriendClickListener != null) {
                    mOnFriendClickListener.onFriendClick(profileData);
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ProfileViewHolder holder, int position) {
        ProfileData profileData = mFriends.get(position);
        holder.personNameView.setText(String.format(Locale.getDefault(), "%s %s", profileData.getGivenName(), profileData.getFamilyName()));

        Picasso.with(holder.itemView.getContext())
                .load(profileData.getImage())
                .transform(new CircleTransformation())
                .into(holder.profileImageView);
    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }

    public void addFriends(List<ProfileData> friends) {
        mFriends.addAll(friends);
        notifyItemRangeInserted(0, mFriends.size());
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {
        private final ImageView profileImageView;
        private final TextView personNameView;

        ProfileViewHolder(View itemView) {
            super(itemView);
            profileImageView = (ImageView) itemView.findViewById(R.id.profileImageView);
            personNameView = (TextView) itemView.findViewById(R.id.personNameView);
        }
    }
}
