package com.tantv.vnradiotruyen.activities;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.tantv.vnradiotruyen.R;
import com.tantv.vnradiotruyen.until.UtilFunctions;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Copyright @2015
 * Created by tantv on 21/10/2015.
 */
@EActivity(R.layout.activity_splash_main)
public class SplashActivity extends AppCompatActivity {
    @ViewById(R.id.progressBar)
    ProgressBar progressBar;
    Intent intent;

    @AfterViews
    void afterView() {
        progressBar.setVisibility(View.VISIBLE);
        getSupportActionBar().hide();
        intent = new Intent(this, MainActivity_.class);
        boolean isServiceRunning = UtilFunctions.isServiceRunning(PlayMusicService.class.getName(), getApplicationContext());
        if (isServiceRunning) {
            startActivity(intent);
            finish();
            return;
        }
        new CountDownTimer(3000, 200) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished < 200) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFinish() {

                startActivity(intent);
                finish();
            }
        }.start();
    }
}
