package com.tantv.vnradiotruyen.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RelativeLayout;

import com.tantv.vnradiotruyen.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * Created by tantv on 16/10/2015.
 */
@EFragment(R.layout.fragment_main_controller)
public class MainControllerFragment extends Fragment {
    @ViewById(R.id.rlAdList)
    RelativeLayout mRlAdList;

    @AfterViews
    void afterView() {
        loadAd();
    }

    AdView adView;
    AdRequest adRequest;
    public void loadAd() {
        adView = new AdView(getActivity());
        adView.setAdSize(AdSize.SMART_BANNER);
        adRequest = new AdRequest.Builder()
                // Add a test device to show Test Ads
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("134A1CA55349046A9E073083486C581E")
                .build();
        adView.setAdUnitId("ca-app-pub-5675731296915316/6861481986");
        RelativeLayout.LayoutParams labelLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        labelLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mRlAdList.addView(adView, labelLayoutParams);
        mRlAdList.setVisibility(View.GONE);
        adView.loadAd(adRequest);
        mRlAdList.setVisibility(View.VISIBLE);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);

            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                mRlAdList.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mRlAdList.setVisibility(View.VISIBLE);
            }
        });
    }
}
