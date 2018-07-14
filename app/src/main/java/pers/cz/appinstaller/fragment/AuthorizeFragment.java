package pers.cz.appinstaller.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    private String apkAbsolutePath;
    private String packageName;
    private boolean deleteAfterFinish;
    private FragmentManager fragmentManager;
    private EditText editText;
    private TextView copyright;
    private Button sureBtn;
    private final String AuthorizeUrl = "";

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
        fragmentManager = getActivity().getSupportFragmentManager();
        this.editText = (EditText) getActivity().findViewById(R.id.authorizeCode);
        this.copyright = (TextView) getActivity().findViewById(R.id.copyright);
        String machineId = InstallUtil.getMachineId();
        sureBtn = (Button) getActivity().findViewById(R.id.sure);
        if (machineId != null && !machineId.isEmpty()) {
            AsyncNetUtil.post(AuthorizeUrl, machineId, new ShowTextCallback<String>() {
                @Override
                public void setText(String text) {
                    if (text != null && text.equals("enable"))
                        sureBtn.setEnabled(true);
                }
            });
            machineId = "序列号：" + machineId;
            ((TextView) getActivity().findViewById(R.id.machineId)).setText(machineId);
        }
        sureBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AsyncNetUtil.post(AuthorizeUrl, AuthorizeFragment.this.editText.getText().toString(), AuthorizeFragment.this);
            }
        });
        getActivity().findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
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
    }

    @Override
    public void setText(String text) {
        if (text == null || !text.equals("passed"))
            return;
        fragmentManager.popBackStackImmediate();
        ProgressFragment fragment = new ProgressFragment();
        Bundle arguments = new Bundle();
        arguments.putString("apkAbsolutePath", AuthorizeFragment.this.apkAbsolutePath);
        arguments.putString("packageName", AuthorizeFragment.this.packageName);
        arguments.putBoolean("deleteAfterFinish", AuthorizeFragment.this.deleteAfterFinish);
        fragment.setArguments(arguments);
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment, fragment)
                .addToBackStack(null)
                .commit();
    }
}
