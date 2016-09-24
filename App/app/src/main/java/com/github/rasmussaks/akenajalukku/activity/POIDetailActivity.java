package com.github.rasmussaks.akenajalukku.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.rasmussaks.akenajalukku.R;
import com.github.rasmussaks.akenajalukku.model.PointOfInterest;
import com.github.rasmussaks.akenajalukku.util.AppBarStateChangeListener;

public class POIDetailActivity extends AppCompatActivity {
    private AppBarStateChangeListener.State lastRealState = AppBarStateChangeListener.State.EXPANDED;
    private PointOfInterest poi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        poi = intent.getParcelableExtra("poi");

        setContentView(R.layout.activity_poidetail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(poi.getTitle());
            ((TextView) findViewById(R.id.poi_description)).setText(poi.getDescription());
        }


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                Log.v("aken-ajalukku", state.toString());
                if (state == State.COLLAPSED && lastRealState != state) {
                    fab.hide();
                    lastRealState = state;
                } else if (state == State.EXPANDED && lastRealState != state) {
                    fab.show();
                    lastRealState = state;
                }
            }
        });
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        Glide.with(this).load(poi.getImageUrl()).centerCrop().into(iv);

        // Check if the version of Android is Lollipop or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        }

        //Workaround for ImageView not removing window insets when fitsSystemWindows=true
        //https://code.google.com/p/android/issues/detail?id=220389
        ViewCompat.setOnApplyWindowInsetsListener(iv, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v,
                                                          WindowInsetsCompat insets) {
                return insets.consumeSystemWindowInsets();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
