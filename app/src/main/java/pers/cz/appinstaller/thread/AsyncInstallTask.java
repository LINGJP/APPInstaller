package pers.cz.appinstaller.thread;

import android.os.AsyncTask;
import pers.cz.appinstaller.R;
import pers.cz.appinstaller.callback.ShowTextCallback;
import pers.cz.appinstaller.util.InstallUtil;

import java.io.File;

public class AsyncInstallTask extends AsyncTask<String, Void, String> {
    private ShowTextCallback<Integer> callback;
    private final boolean deleteAfterFinish;

    public AsyncInstallTask(ShowTextCallback<Integer> callback, boolean deleteAfterFinish) {
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
            if (InstallUtil.checkApkIfEncode(apkAbsolutePath) == 19)
                apkAbsolutePath = InstallUtil.decodeApkFile(apkAbsolutePath);
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
        if (result.equals("outOfMemory"))
            callback.setText(R.string.out_of_space_dlg_text);
        else if (result.contains("Success"))
            callback.setText(R.string.install_success);
        else
            callback.setText(R.string.install_fail);
    }
}