package pers.cz.appinstaller;

import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.*;
import pers.cz.appinstaller.fragment.PermissionFragment;
import pers.cz.appinstaller.fragment.ReplaceFragment;
import pers.cz.appinstaller.util.InstallUtil;

import java.io.File;

public class PackageInstallActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_dialog);
        String apkAbsolutePath = getIntent().getStringExtra("apkAbsolutePath");
        if (apkAbsolutePath == null || apkAbsolutePath.equals("")) {
            Uri packageURI = getIntent().getData();
            assert packageURI != null;
            String scheme = packageURI.getScheme();
            if (scheme != null && !"file".equals(scheme))
                throw new IllegalArgumentException("unexpected scheme " + scheme);
            apkAbsolutePath = new File(packageURI.getPath()).getAbsolutePath();
        }
//        apkAbsolutePath = "/storage/sdcard/backups/apps/ES文件浏览器_4.1.7.2.apk";
        //draw header
        Drawable drawable = InstallUtil.getAppIconByByFilePath(this, apkAbsolutePath);
        if (drawable != null)
            ((ImageView) findViewById(R.id.appIcon)).setImageDrawable(drawable);
        ((TextView) findViewById(R.id.appName)).setText(InstallUtil.getAppNameByFilePath(this, apkAbsolutePath));
        TextView appVersionTextView = (TextView) findViewById(R.id.appVersion);
        appVersionTextView.setText(InstallUtil.getAppVersionByFilePath(this, apkAbsolutePath));
        String packageName = InstallUtil.getPackageNameByFilePath(this, apkAbsolutePath);
        //draw content
        Fragment fragment;
        PackageInfo packageInfo = InstallUtil.getPackageInfoByPackageName(this, packageName);
        if (null == packageInfo)
            fragment = new PermissionFragment();
        else {
            String appVersionStr = packageInfo.versionName + " -> " + appVersionTextView.getText();
            appVersionTextView.setText(appVersionStr);
            fragment = new ReplaceFragment();
        }
        Bundle arguments = new Bundle();
        arguments.putString("apkAbsolutePath", apkAbsolutePath);
        arguments.putString("packageName", packageName);
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.slide_out_down, R.anim.slide_out_down);
    }
}
