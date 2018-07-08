package pers.cz.appinstaller;

import android.app.Activity;
import android.view.View;

public class BtnClickListener implements View.OnClickListener {

    private Activity activity;
    private String packageName;

    public BtnClickListener(Activity activity, String packageName) {
        this.activity = activity;
        this.packageName = packageName;
    }

    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.sure:
                    activity.finish();
                    break;
                case R.id.finish:
                    activity.finish();
                    break;
                case R.id.open:
                    Util.openApp(this.packageName, activity);
                    activity.finish();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}