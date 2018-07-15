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
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.setText(NetUtil.get(url));
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
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.setText(NetUtil.post(url, content));
                    }
                });
            }
        }.start();
    }
}
