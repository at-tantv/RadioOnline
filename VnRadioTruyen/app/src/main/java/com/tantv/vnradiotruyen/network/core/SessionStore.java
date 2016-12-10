package com.tantv.vnradiotruyen.network.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Map;

/**
 * Created by tientun on 3/9/15.
 */
public abstract class SessionStore<T> {

    public boolean save(Context context, T data) {
        Editor editor =
                context.getSharedPreferences(getPrivateKey(), Context.MODE_PRIVATE).edit();
        Map<String, String> session = saveSession(data);
        for (Map.Entry<String, String> entry : session.entrySet()) {
            editor.putString(entry.getKey(), entry.getValue());
        }
        return editor.commit();
    }

    public T restore(Context context) {
        SharedPreferences savedSession = context.getSharedPreferences(getPrivateKey(), Context.MODE_PRIVATE);
        return restoreSession(savedSession.getAll());
    }

    public boolean isAuthenticated(Context context) {
        try {
            return checkAuthenticated(restore(context));
        } catch (NullPointerException e) {
            return false;
        }
    }

    public Map<String, String> header(Context context) {
        return getHeader(restore(context));
    }

    public Class<?> getSessionClass() {
        return restoreSession(null).getClass();
    }

    protected abstract Map<String, String> saveSession(T data);

    protected abstract Map<String, String> getHeader(T data);

    protected abstract T restoreSession(Map<String, ?> sessions);

    protected abstract String getPrivateKey();

    public abstract boolean checkAuthenticated(T data);

    public void clear(Context context) {
        Editor editor =
                context.getSharedPreferences(getPrivateKey(), Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }
}
