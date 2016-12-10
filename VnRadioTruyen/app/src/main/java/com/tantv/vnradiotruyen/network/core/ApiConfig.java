package com.tantv.vnradiotruyen.network.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by tientun on 7/2/15.
 */
public class ApiConfig {
    SessionStore sessionStore;
    Context mContext;
    String baseUrl;

    public ApiConfig(Builder builder) {
        this.sessionStore = builder.sessionStore;
        this.mContext = builder.mContext;
        this.baseUrl = builder.baseUrl;
    }

    public static Builder builder(Context context){
        return new Builder(context);
    }

    public static class Builder {
        SessionStore sessionStore;
        Context mContext;
        String baseUrl;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder sessionStore(@Nullable SessionStore sessionStore) {
            this.sessionStore = sessionStore;
            return this;
        }

        public Builder baseUrl(@NonNull String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public ApiConfig build() {
            return new ApiConfig(this);
        }
    }

}
