package com.github.rasmussaks.akenajalukku.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.github.rasmussaks.akenajalukku.R;

public class VideoPlayerActivity extends AppCompatActivity implements EasyVideoCallback {
    private EasyVideoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        Intent intent = getIntent();

        String url = intent.getStringExtra("url");
        String title = intent.getStringExtra("title");
        ActionBar bar = getSupportActionBar();
        if (bar != null) {

            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(title);
        }

        player = (EasyVideoPlayer) findViewById(R.id.video_player);

        player.setCallback(this);
        player.setSource(Uri.parse(url));
    }

    @Override
    public void onStarted(EasyVideoPlayer player) {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.hide();
        }
    }

    @Override
    public void onPaused(EasyVideoPlayer player) {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPreparing(EasyVideoPlayer player) {

    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {

    }

    @Override
    public void onBuffering(int percent) {

    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {

    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.show();
        }
    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {

    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {

    }
}
