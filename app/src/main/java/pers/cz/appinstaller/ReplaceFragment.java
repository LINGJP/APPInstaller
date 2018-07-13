package pers.cz.appinstaller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ReplaceFragment extends android.support.v4.app.Fragment {
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
        getActivity().findViewById(R.id.replace).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PermissionFragment fragment = new PermissionFragment();
                Bundle arguments = new Bundle();
                arguments.putString("apkAbsolutePath", ReplaceFragment.this.apkAbsolutePath);
                arguments.putString("packageName", ReplaceFragment.this.packageName);
                fragment.setArguments(arguments);
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.fragment, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        getActivity().findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().finish();
            }
        });
    }
}
