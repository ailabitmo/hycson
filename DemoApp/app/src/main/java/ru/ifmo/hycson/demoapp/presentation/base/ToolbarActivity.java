package ru.ifmo.hycson.demoapp.presentation.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import ru.ifmo.hycson.demoapp.R;

public abstract class ToolbarActivity extends AppCompatActivity {

    @LayoutRes
    protected abstract int getLayoutRes();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarView);
        setSupportActionBar(toolbar);
    }
}
