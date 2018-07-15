package pers.cz.appinstaller.fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import pers.cz.appinstaller.R;
import pers.cz.appinstaller.callback.ShowTextCallback;
import pers.cz.appinstaller.thread.AsyncInstallTask;
import pers.cz.appinstaller.util.InstallUtil;

public class ProgressFragment extends Fragment implements ShowTextCallback<Integer> {
    private FragmentActivity activity;
    private TextView informationTextView;
    private ImageView progressImg;
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
        progressImg = (ImageView) activity.findViewById(R.id.progressImg);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    InstallUtil.openApp(ProgressFragment.this.packageName, ProgressFragment.this.activity);
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
        new AsyncInstallTask(this, this.deleteAfterFinish).execute(this.apkAbsolutePath);
    }

    @Override
    public void setText(Integer text) {
        informationTextView.setText(text);
        progressImg.setVisibility(View.INVISIBLE);
        switch (text) {
            case R.string.install_success:
                openButton.setEnabled(true);
            default:
                finishButton.setEnabled(true);
                break;
        }
    }
}
