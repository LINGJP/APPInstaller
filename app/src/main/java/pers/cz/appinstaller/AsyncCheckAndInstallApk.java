package pers.cz.appinstaller;

import android.os.AsyncTask;

import java.io.File;

public class AsyncCheckAndInstallApk extends AsyncTask<String, Void, String> {
    private ShowTextCallback<Integer> callback;
    private final boolean deleteAfterFinish;

    public AsyncCheckAndInstallApk(ShowTextCallback<Integer> callback, boolean deleteAfterFinish) {
        this.callback = callback;
        this.deleteAfterFinish = deleteAfterFinish;
    }

    @Override
    protected void onPreExecute() {
        callback.setText(R.string.installing);
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "fail";
        if (params.length > 0) {
            String apkAbsolutePath = params[0];
            if (Util.checkApkIfEncode(apkAbsolutePath) == 19)
                apkAbsolutePath = Util.decodeApkFile(apkAbsolutePath);
            if (apkAbsolutePath != null)
                result = InstallUtil.installApk(apkAbsolutePath);
            if (deleteAfterFinish && result.contains("Success")) {
                new File(params[0]).delete();
                new File(apkAbsolutePath).delete();
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result.contains("Success"))
            callback.setText(R.string.install_success);
        else
            callback.setText(R.string.install_fail);
    }
}