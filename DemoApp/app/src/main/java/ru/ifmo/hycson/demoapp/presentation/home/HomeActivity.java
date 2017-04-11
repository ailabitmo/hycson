package ru.ifmo.hycson.demoapp.presentation.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;

import javax.inject.Inject;

import ru.ifmo.hycson.demoapp.App;
import ru.ifmo.hycson.demoapp.R;
import ru.ifmo.hycson.demoapp.data.PreferencesManager;
import ru.ifmo.hycson.demoapp.presentation.auth.BaseAuthActivity;
import ru.ifmo.hycson.demoapp.presentation.auth.SelectedSocialNetwork;
import ru.ifmo.hycson.demoapp.presentation.auth.TwitterAuthActivity;
import ru.ifmo.hycson.demoapp.presentation.auth.VKAuthActivity;
import ru.ifmo.hymp.entities.Link;
import ru.ifmo.hymp.entities.Resource;

public class HomeActivity extends MvpActivity<HomeContract.View, HomeContract.Presenter>
        implements HomeContract.View, View.OnClickListener {

    private DrawerLayout mDrawer;
    private Menu mNavigationMenu;

    private ViewGroup mHeaderRootView;
    private View mHeaderVkLogoView;
    private View mHeaderTwitterLogoView;
    private TextView mHeaderGreetingTextView;

    @Inject
    PreferencesManager mPreferencesManager;

    private SelectedSocialNetwork mSelectedSocialNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        App.getApp(this).getAppComponent().inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarView);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_toolbar_stat_hamburger_24dp);

        mDrawer = (DrawerLayout) findViewById(R.id.drawerView);
        NavigationView navigationView = (NavigationView) mDrawer.findViewById(R.id.navigationView);
        mNavigationMenu = navigationView.getMenu();

        ViewGroup headerView = (ViewGroup) navigationView.getHeaderView(0);
        mHeaderRootView = (ViewGroup) headerView.findViewById(R.id.rootView);

        mHeaderVkLogoView = headerView.findViewById(R.id.vkLogoView);
        mHeaderVkLogoView.setOnClickListener(this);

        mHeaderTwitterLogoView = headerView.findViewById(R.id.twitterLogoView);
        mHeaderTwitterLogoView.setOnClickListener(this);

        mHeaderGreetingTextView = (TextView) headerView.findViewById(R.id.greetingView);

        mSelectedSocialNetwork = mPreferencesManager.retrieveSelectedSocialNetwork();
        setHeader(mSelectedSocialNetwork);
        if (mSelectedSocialNetwork != SelectedSocialNetwork.NON) {
            getPresenter().loadEntryPoint();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
        } else {
            String accessToken = data.getStringExtra(BaseAuthActivity.EXTRA_ACCESS_TOKEN);

            if (requestCode == SelectedSocialNetwork.VK.ordinal()) {
                mPreferencesManager.saveAccessToken(SelectedSocialNetwork.VK.getKey(), accessToken);
                setSelectedSocialNetwork(SelectedSocialNetwork.VK);
            } else if (requestCode == SelectedSocialNetwork.TWITTER.ordinal()) {
                mPreferencesManager.saveAccessToken(SelectedSocialNetwork.TWITTER.getKey(), accessToken);
                setSelectedSocialNetwork(SelectedSocialNetwork.TWITTER);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vkLogoView:
                onSocialNetworkIconClick(SelectedSocialNetwork.VK, VKAuthActivity.prepareStartIntent(this));
                break;
            case R.id.twitterLogoView:
                onSocialNetworkIconClick(SelectedSocialNetwork.TWITTER, TwitterAuthActivity.prepareStartIntent(this));
                break;
        }
    }

    @NonNull
    @Override
    public HomeContract.Presenter createPresenter() {
        return App.getApp(this).plusHympComponent().plusHomeComponent().presenter();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showError(Throwable e) {
        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        mNavigationMenu.clear();
    }

    @Override
    public void entryPointLoaded(Resource entryPointResource) {
        mNavigationMenu.clear();
        for (final Link link : entryPointResource.getLinks()) {
            mNavigationMenu.add(link.getTitle()).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(HomeActivity.this, link.getValue(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawer.openDrawer(GravityCompat.START);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    private void onSocialNetworkIconClick(SelectedSocialNetwork selectedNetwork, Intent authIntent) {
        if (!TextUtils.isEmpty(mPreferencesManager.retrieveAccessToken(selectedNetwork.getKey()))) {
            setSelectedSocialNetwork(selectedNetwork);
        } else {
            startActivityForResult(authIntent, selectedNetwork.ordinal());
        }
    }

    private void setSelectedSocialNetwork(SelectedSocialNetwork selectedNetwork) {
        if (mSelectedSocialNetwork == selectedNetwork) {
            return;
        }

        mSelectedSocialNetwork = selectedNetwork;
        mPreferencesManager.saveSelectedSocialNetwork(selectedNetwork);

        setHeader(selectedNetwork);

        App.getApp(this).clearHympComponent();
        HomeContract.Presenter newPresenter = App.getApp(this).plusHympComponent().plusHomeComponent().presenter();
        newPresenter.attachView(this);
        setPresenter(newPresenter);

        getPresenter().loadEntryPoint();
    }

    private void setHeader(SelectedSocialNetwork selectedNetwork) {
        @StringRes int titleRes = selectedNetwork == SelectedSocialNetwork.NON ? R.string.non_greeting :
                (selectedNetwork == SelectedSocialNetwork.VK ? R.string.vk_auth_greeting : R.string.twitter_auth_greeting);
        mHeaderGreetingTextView.setText(titleRes);

        setSelectedHeaderIcon(selectedNetwork);
    }

    private void setSelectedHeaderIcon(SelectedSocialNetwork selectedNetwork) {
        View selectedIcon, nonSelectedIcon;
        TransitionManager.beginDelayedTransition(mHeaderRootView, new ChangeBounds());

        switch (selectedNetwork) {
            case VK:
                selectedIcon = mHeaderVkLogoView;
                nonSelectedIcon = mHeaderTwitterLogoView;
                break;
            case TWITTER:
                selectedIcon = mHeaderTwitterLogoView;
                nonSelectedIcon = mHeaderVkLogoView;
                break;
            default:
                setNotSelectedHeaderIcons();
                return;
        }

        int selectedIconSize = (int) getResources().getDimension(R.dimen.drawer_header_selected_icon_size);
        RelativeLayout.LayoutParams selectedParams = new RelativeLayout.LayoutParams(selectedIconSize, selectedIconSize);
        selectedParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        selectedParams.addRule(RelativeLayout.ALIGN_START, R.id.greetingView);
        selectedIcon.setLayoutParams(selectedParams);

        int standardIconSize = (int) getResources().getDimension(R.dimen.drawer_header_standard_icon_size);
        RelativeLayout.LayoutParams nonSelectedParams = new RelativeLayout.LayoutParams(standardIconSize, standardIconSize);
        nonSelectedParams.addRule(RelativeLayout.ALIGN_TOP, selectedIcon.getId());
        nonSelectedParams.addRule(RelativeLayout.ALIGN_END, R.id.greetingView);
        nonSelectedIcon.setLayoutParams(nonSelectedParams);
    }

    private void setNotSelectedHeaderIcons() {
        int standardIconSize = (int) getResources().getDimension(R.dimen.drawer_header_standard_icon_size);
        RelativeLayout.LayoutParams vkParams = new RelativeLayout.LayoutParams(standardIconSize, standardIconSize);
        RelativeLayout.LayoutParams twitterParams = new RelativeLayout.LayoutParams(standardIconSize, standardIconSize);

        vkParams.addRule(RelativeLayout.ALIGN_END, R.id.greetingView);
        vkParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

        twitterParams.addRule(RelativeLayout.START_OF, mHeaderVkLogoView.getId());
        twitterParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

        mHeaderVkLogoView.setLayoutParams(vkParams);
        mHeaderTwitterLogoView.setLayoutParams(twitterParams);
    }
}