package pers.cz.appinstaller;

import android.os.AsyncTask;

public class AsyncCheckAndInstallApk extends AsyncTask<String, Void, String> {
    private ActivityCallback<Integer> callback;

    public AsyncCheckAndInstallApk(ActivityCallback<Integer> callback) {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "fail";
        if (params.length > 0) {
            if (Util.checkApkIfEncode(params[0]) == 19)
                params[0] = Util.decodeApkFile(params[0]);
            if (params[0] != null)
                result = InstallUtil.installApk(params[0]);
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result.contains("Success"))
            callback.setText(R.string.install_success);
        else
            callback.setText(R.string.install_faill);
    }
}