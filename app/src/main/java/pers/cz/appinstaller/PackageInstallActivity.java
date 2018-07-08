package pers.cz.appinstaller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

@SuppressLint({"NewApi"})
public class PackageInstallActivity extends Activity implements ActivityCallback<Integer> {
    private RelativeLayout installLayout;
    private TextView informationTextView;
    private ProgressBar progressBar;
    private String apkAbsolutePath;
    private String packageName;

    @SuppressLint({"NewApi"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showReplaceView();
/*
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.apkAbsolutePath = getIntent().getStringExtra("apkAbsolutePath");
        if (this.apkAbsolutePath == null || this.apkAbsolutePath.equals("")) {
            Uri packageURI = getIntent().getData();
            if (packageURI != null) {
                String scheme = packageURI.getScheme();
                if (scheme != null && !"file".equals(scheme))
                    throw new IllegalArgumentException("unexpected scheme " + scheme);
                this.apkAbsolutePath = new File(packageURI.getPath()).getAbsolutePath();
            }
        }
        this.packageName = Util.getPackageNameByFilePath(this, this.apkAbsolutePath);
        if (null != Util.getPackageInfoByPackageName(this, this.packageName))
            showInstallView();
        else
            showReplaceView();
*/
    }

    private void showInstallView() {
        setContentView(R.layout.install_progress_dialog);
        ImageView appIcon = (ImageView) findViewById(R.id.appIcon);
        TextView appNameTextView = (TextView) findViewById(R.id.appName);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.informationTextView = (TextView) findViewById(R.id.information);
        Button finishButton = (Button) findViewById(R.id.finish);
        Button openButton = (Button) findViewById(R.id.open);
        Button sureButton = (Button) findViewById(R.id.sure);
        this.installLayout = (RelativeLayout) findViewById(R.id.install_button_layout);
        this.installLayout.setVisibility(View.GONE);
        BtnClickListener btnClickListener = new BtnClickListener(this, this.packageName);
        finishButton.setOnClickListener(btnClickListener);
        openButton.setOnClickListener(btnClickListener);
        sureButton.setOnClickListener(btnClickListener);
        String appName = Util.getAppNameByFilePath(this, this.apkAbsolutePath);
        Drawable drawable = Util.getAppIconByByFilePath(this, this.apkAbsolutePath);
        if (drawable != null)
            appIcon.setBackground(drawable);
        this.informationTextView.setText(this.getString(R.string.installing));
        new AsyncCheckAndInstallApk(this).execute(this.apkAbsolutePath);
        appNameTextView.setText(appName);
    }

    private void showReplaceView() {
        setContentView(R.layout.app_has_exit_replace_dialog);
        findViewById(R.id.sure).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                PackageInstallActivity.this.showInstallView();
            }
        });
        findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                PackageInstallActivity.this.finish();
            }
        });
    }

    @Override
    public void setText(Integer strId) {
        informationTextView.setText(strId);
        this.installLayout.setVisibility(View.VISIBLE);
        this.progressBar.setVisibility(View.GONE);
    }

}
