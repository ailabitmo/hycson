package ru.ifmo.hycson.demoapp.presentation.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import ru.ifmo.hycson.demoapp.R;


/**
 * This is base single-fragment activity
 */
public abstract class BaseActivity extends AppCompatActivity {

    @LayoutRes
    protected abstract int getLayoutRes();

    @Nullable
    protected abstract Fragment createDisplayedFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());

        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = createDisplayedFragment();

            if (fragment != null) {
                fragmentManager.beginTransaction()
                        .add(R.id.fragmentContainer, fragment)
                        .commit();
            }
        }
    }
}