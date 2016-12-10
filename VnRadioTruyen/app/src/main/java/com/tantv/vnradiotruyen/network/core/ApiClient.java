package com.tantv.vnradiotruyen.network.core;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.tantv.vnradiotruyen.network.Api;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

public class ApiClient {

    private static final String HEADER_UA = "User-Agent";
    private static final String TAG = ApiClient.class.getSimpleName();

    private static final int TIMEOUT_CONNECTION = 10000;

    private static ApiClient sApiClient;
    /**
     * OkHttpClient:by OkHttp
     */
    private OkHttpClient mOkHttpClient;
    /**
     * RestAdapter:by retrofit
     */
    private RestAdapter mRestAdapter;
    /**
     * Api service
     */
    public Api api;
    /**
     * android application context
     */
    private Context mContext;

    SessionStore sessionStore;

    /**
     * Headers that need to be added to every request can be specified using a RequestInterceptor
     */
    private RequestInterceptor mRequestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            Map<String, String> headers = sessionStore.header(mContext);
            if (headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    request.addHeader(entry.getKey(), entry.getValue());
                }
            }
            request.addHeader(HEADER_UA, createUserAgent());
        }
    };

    public void init(ApiConfig apiConfig) {
        Log.d(TAG, "initialize start");
        mContext = apiConfig.mContext;
        sessionStore = apiConfig.sessionStore;
        // Gson rules
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        // initialize OkHttpClient
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(TIMEOUT_CONNECTION, TimeUnit.MILLISECONDS);

        // initialize RestAdapter
        mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(apiConfig.baseUrl)
                .setClient(new OkClient(mOkHttpClient))
                .setRequestInterceptor(mRequestInterceptor)
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        // implementation of the TryApi interface.
        api = mRestAdapter.create(Api.class);
        Log.d(TAG, "initialize end");
    }

    public static Api call() {
        return getInstance().api;
    }

    /**
     * get singleton instance
     *
     * @return
     */
    public static synchronized ApiClient getInstance() {
        if (sApiClient == null) {
            sApiClient = new ApiClient();
        }
        return sApiClient;
    }

    public SessionStore getSessionStore() {
        return this.sessionStore;
    }

    public boolean isAuthenticated() {
        return this.sessionStore.isAuthenticated(mContext);
    }

    public void saveSession(Object object) {
        this.sessionStore.save(mContext, object);
    }

    private String createUserAgent() {
        PackageManager pm = mContext.getPackageManager();
        String versionName = "";
        try {
            PackageInfo packageInfo = pm.getPackageInfo(mContext.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return System.getProperty("http.agent") + " " + mContext.getPackageName() + "/" + versionName;
    }


}
