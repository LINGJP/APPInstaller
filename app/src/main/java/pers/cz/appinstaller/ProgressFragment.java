package pers.cz.appinstaller;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ProgressFragment extends Fragment implements ShowTextCallback<Integer> {
    private FragmentActivity activity;
    private TextView informationTextView;
    private Button openButton;
    private Button finishButton;
    private String apkAbsolutePath;
    private String packageName;
    private boolean deleteAfterFinish;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        this.apkAbsolutePath = arguments.getString("apkAbsolutePath");
        this.packageName = arguments.getString("packageName");
        this.deleteAfterFinish = arguments.getBoolean("deleteAfterFinish");
        return inflater.inflate(R.layout.progress_dialog, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        informationTextView = (TextView) activity.findViewById(R.id.information);
        openButton = (Button) activity.findViewById(R.id.open);
        finishButton = (Button) activity.findViewById(R.id.finish);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Util.openApp(ProgressFragment.this.packageName, ProgressFragment.this.activity);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                activity.finish();
            }
        });
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });
        new AsyncCheckAndInstallApk(this, this.deleteAfterFinish).execute(this.apkAbsolutePath);
    }

    @Override
    public void setText(Integer text) {
        informationTextView.setText(text);
        switch (text) {
            case R.string.install_success:
                openButton.setEnabled(true);
            case R.string.install_fail:
                finishButton.setEnabled(true);
                break;
            default:
                break;
        }
    }
}
