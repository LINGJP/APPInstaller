package pers.cz.appinstaller.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import pers.cz.appinstaller.R;

public class ReplaceFragment extends android.support.v4.app.Fragment {
    private FragmentActivity activity;
    private FragmentManager supportFragmentManager;
    private String apkAbsolutePath;
    private String packageName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        this.apkAbsolutePath = arguments.getString("apkAbsolutePath");
        this.packageName = arguments.getString("packageName");
        return inflater.inflate(R.layout.replace_dialog, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        supportFragmentManager = activity.getSupportFragmentManager();
        activity.findViewById(R.id.sureReplace).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PermissionFragment fragment = new PermissionFragment();
                Bundle arguments = new Bundle();
                arguments.putString("apkAbsolutePath", ReplaceFragment.this.apkAbsolutePath);
                arguments.putString("packageName", ReplaceFragment.this.packageName);
                fragment.setArguments(arguments);
                supportFragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.fragment, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        activity.findViewById(R.id.cancelReplace).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                supportFragmentManager.popBackStack();
                activity.finish();
            }
        });
    }
}
