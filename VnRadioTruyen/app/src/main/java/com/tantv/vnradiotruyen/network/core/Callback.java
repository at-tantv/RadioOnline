package com.tantv.vnradiotruyen.network.core;

import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class Callback<T> implements retrofit.Callback<T> {

    private static final String TAG = Callback.class.getSimpleName();

    public Callback() {
    }

    public abstract void success(T t);

    public abstract void failure(RetrofitError error, Error myError);

    @Override
    public void success(T t, Response response) {
        if (t.getClass().equals(ApiClient.getInstance().getSessionStore().getSessionClass())) {
            ApiClient.getInstance().saveSession(t);
        }
        success(t);
    }

    public void failure(RetrofitError error) {
        if (error.getResponse() != null) {
            Error myError = (Error) error.getBodyAs(Error.class);
            failure(error, myError);
        }
        failure(error, new Error("0", null));
    }

}
