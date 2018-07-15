package pers.cz.appinstaller.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import pers.cz.appinstaller.R;
import pers.cz.appinstaller.callback.ShowTextCallback;
import pers.cz.appinstaller.util.AsyncNetUtil;
import pers.cz.appinstaller.util.InstallUtil;

/**
 * Authorize Fragment
 *
 * @author 橙子
 * @date 2018/7/13
 */
public class AuthorizeFragment extends android.support.v4.app.Fragment implements ShowTextCallback<String> {
    private FragmentActivity activity;
    private FragmentManager supportFragmentManager;
    private EditText editText;
    private TextView copyright;
    private Button sureBtn;
    private String apkAbsolutePath;
    private String packageName;
    private String machineId;
    private boolean deleteAfterFinish;
    private final String AuthorizeUrl = "http://auth.orange.pers.ngrok.xiaomiqiu.cn";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        this.apkAbsolutePath = arguments.getString("apkAbsolutePath");
        this.packageName = arguments.getString("packageName");
        this.deleteAfterFinish = arguments.getBoolean("deleteAfterFinish");
        return inflater.inflate(R.layout.authorize_dialog, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.activity = getActivity();
        supportFragmentManager = activity.getSupportFragmentManager();
        this.editText = (EditText) activity.findViewById(R.id.authorizeCode);
        this.copyright = (TextView) activity.findViewById(R.id.copyright);
        machineId = InstallUtil.getMachineId();
        sureBtn = (Button) activity.findViewById(R.id.sureAuthorize);
        sureBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AsyncNetUtil.post(AuthorizeUrl, "machineId=" + machineId + "&authorize=" + AuthorizeFragment.this.editText.getText().toString(), AuthorizeFragment.this);
            }
        });
        activity.findViewById(R.id.cancelAuthorize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFragmentManager.popBackStack();
            }
        });
        AsyncNetUtil.get(AuthorizeUrl, new ShowTextCallback<String>() {
            @Override
            public void setText(String text) {
                if (text == null)
                    text = "连接失败，请开启网络后重试";
                copyright.setText(text);
            }
        });
        if (!machineId.isEmpty()) {
            AsyncNetUtil.post(AuthorizeUrl, "machineId=" + machineId, new ShowTextCallback<String>() {
                @Override
                public void setText(String text) {
                    if (text != null && text.equals("enable"))
                        sureBtn.setEnabled(true);
                }
            });
            String machineIdStr = "序列号 " + machineId;
            ((TextView) activity.findViewById(R.id.machineId)).setText(machineIdStr);
            ClipboardManager mClipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText("Label", machineIdStr);
            mClipboardManager.setPrimaryClip(mClipData);
            Toast.makeText(activity, "已将序列号复制置剪切板", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setText(String text) {
        if (text == null || !text.equals("passed")) {
            copyright.setText(R.string.authorize_fail);
            return;
        }
        ProgressFragment fragment = new ProgressFragment();
        Bundle arguments = new Bundle();
        arguments.putString("apkAbsolutePath", AuthorizeFragment.this.apkAbsolutePath);
        arguments.putString("packageName", AuthorizeFragment.this.packageName);
        arguments.putBoolean("deleteAfterFinish", AuthorizeFragment.this.deleteAfterFinish);
        fragment.setArguments(arguments);
        supportFragmentManager.popBackStackImmediate();
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment, fragment)
                .commit();
    }
}
