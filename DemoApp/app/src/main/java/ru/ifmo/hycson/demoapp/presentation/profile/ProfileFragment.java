package ru.ifmo.hycson.demoapp.presentation.profile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpFragment;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import ru.ifmo.hycson.demoapp.App;
import ru.ifmo.hycson.demoapp.R;
import ru.ifmo.hycson.demoapp.presentation.friends.FriendsFragment;
import ru.ifmo.hycson.demoapp.presentation.navigation.links.AppLink;
import ru.ifmo.hycson.demoapp.presentation.navigation.links.create.MessageCreateLink;
import ru.ifmo.hycson.demoapp.presentation.navigation.links.display.FriendsDisplayAppLink;
import ru.ifmo.hycson.demoapp.presentation.profile.entities.ProfileData;

public class ProfileFragment extends MvpFragment<ProfileContract.View, ProfileContract.Presenter>
        implements ProfileContract.View {

    private static final String BUNDLE_PROFILE_URL = "ru.ifmo.hycson.demoapp.presentation.profile.profile_url";

    private View mRootView;
    private ImageView mProfileImageView;
    private TextView mPersonNameView;
    private Button mFriendsButtonView;
    private Button mNewMessageButtonView;
    private View mProgressView;

    @Nullable
    private AlertDialog mAlertDialog;

    private ProfileData mProfileData;

    public static Fragment newInstance(String profileUrl) {
        Fragment instance = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_PROFILE_URL, profileUrl);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mProfileData == null) {
            getPresenter().loadProfileData(getArguments().getString(BUNDLE_PROFILE_URL));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.fragment_profile_title));

        mRootView = view.findViewById(R.id.rootView);
        mProfileImageView = (ImageView) mRootView.findViewById(R.id.profileImageView);
        mPersonNameView = (TextView) view.findViewById(R.id.personNameView);
        mFriendsButtonView = (Button) view.findViewById(R.id.friendsButtonView);
        mNewMessageButtonView = (Button) view.findViewById(R.id.newMessageButtonView);
        mProgressView = view.findViewById(R.id.progressView);

        if (mProfileData != null) {
            updateProfile(mProfileData);
        }
    }

    @NonNull
    @Override
    public ProfileContract.Presenter createPresenter() {
        return App.getApp(getContext()).plusHympComponent().plusProfileComponent().presenter();
    }

    @Override
    public void showLoading(LoadingType type) {
        switch (type) {
            case PROFILE:
                mProgressView.setVisibility(View.VISIBLE);
                break;
            case MESSAGE:
                if (mAlertDialog != null) {
                    View progressView = mAlertDialog.findViewById(R.id.messageProgressView);
                    if (progressView != null) {
                        progressView.setVisibility(View.VISIBLE);
                    }
                }
                break;
        }
    }

    @Override
    public void hideLoading(LoadingType type) {
        switch (type) {
            case PROFILE:
                mProgressView.setVisibility(View.GONE);
                break;
            case MESSAGE:
                if (mAlertDialog != null) {
                    mAlertDialog.dismiss();
                }
                break;
        }
    }

    @Override
    public void showError(Throwable e) {
        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setProfileData(ProfileData profileData) {
        updateProfile(profileData);
    }

    private void updateProfile(final ProfileData profileData) {
        mProfileData = profileData;

        Picasso.with(getContext())
                .load(profileData.getImage())
                .into(mProfileImageView);

        mPersonNameView.setText(String.format(Locale.getDefault(), "%s %s",
                profileData.getGivenName(), profileData.getFamilyName()));

        showSupportedAppLinks(profileData.getAppLinks());
        mRootView.setVisibility(View.VISIBLE);
    }

    private void showSupportedAppLinks(List<AppLink> appLinks) {
        for (final AppLink appLink : appLinks) {
            if (appLink instanceof FriendsDisplayAppLink) {
                mFriendsButtonView.setText(appLink.getTitle());
                mFriendsButtonView.setVisibility(View.VISIBLE);
                mFriendsButtonView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment friendsFragment = FriendsFragment.newInstance(appLink.getUrl(), mProfileData.getGivenName());
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                                .replace(R.id.fragmentContainer, friendsFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                });
            }

            if (appLink instanceof MessageCreateLink) {
                mNewMessageButtonView.setText(appLink.getTitle());
                mNewMessageButtonView.setVisibility(View.VISIBLE);
                mNewMessageButtonView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showComposeMessageDialog(appLink);
                    }
                });
            }
        }
    }

    private void showComposeMessageDialog(final AppLink appLink) {
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.view_new_message_dialog, (ViewGroup) getView(), false);
        // Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.new_message_dialog_title)
                .setView(viewInflated)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String message = input.getText().toString();
                        getPresenter().sendMessage(appLink.getUrl(), message);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        mAlertDialog = builder.show();
    }
}