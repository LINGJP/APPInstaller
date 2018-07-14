package pers.cz.appinstaller.util;

import android.os.Handler;
import pers.cz.appinstaller.callback.ShowTextCallback;

/**
 * a simple util for async http connect
 *
 * @author 橙子
 * @date 2018/7/14
 */
public class AsyncNetUtil {
    public static void get(final String url, final ShowTextCallback<String> callback) {
        final Handler handler = new Handler();
        new Thread() {
            @Override
            public void run() {
                final String response = NetUtil.get(url);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.setText(response);
                    }
                });
            }
        }.start();
    }

    public static void post(final String url, final String content, final ShowTextCallback<String> callback) {
        final Handler handler = new Handler();
        new Thread() {
            @Override
            public void run() {
                final String response = NetUtil.post(url, content);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.setText(response);
                    }
                });
            }
        }.start();
    }
}
