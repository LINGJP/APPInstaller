package pers.cz.appinstaller.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Switch;
import pers.cz.appinstaller.adapter.ExpandableListAdapter;
import pers.cz.appinstaller.R;
import pers.cz.appinstaller.util.InstallUtil;

public class PermissionFragment extends android.support.v4.app.Fragment {
    private FragmentActivity activity;
    private FragmentManager supportFragmentManager;
    private ExpandableListView expandableListView;
    private Switch deleteSwitch;
    private String apkAbsolutePath;
    private String packageName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        this.apkAbsolutePath = arguments.getString("apkAbsolutePath");
        this.packageName = arguments.getString("packageName");
        return inflater.inflate(R.layout.permission_dialog, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        supportFragmentManager = activity.getSupportFragmentManager();
        expandableListView = (ExpandableListView) activity.findViewById(R.id.permissionList);
        deleteSwitch = (Switch) activity.findViewById(R.id.deleteAfterFinish);
        String[] permissions = InstallUtil.getAppPermissionsByFilePath(activity, this.apkAbsolutePath);
        final String[] permissionGroup = new String[]{"包含" + permissions.length + "条权限"};
        String[][] permissionInfo = new String[][]{permissions};
        expandableListView.setAdapter(new ExpandableListAdapter(permissionGroup, permissionInfo, activity, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int groupPosition = (int) view.getTag();
                if (expandableListView.isGroupExpanded(groupPosition))
                    expandableListView.collapseGroup(groupPosition);
                else
                    expandableListView.expandGroup(groupPosition);
            }
        }));
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < permissionGroup.length; i++)
                    if (i != groupPosition)
                        expandableListView.collapseGroup(i);
            }
        });
//        expandableListView.expandGroup(0);
        activity.findViewById(R.id.surePermission).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AuthorizeFragment fragment = new AuthorizeFragment();
                Bundle arguments = new Bundle();
                arguments.putString("apkAbsolutePath", PermissionFragment.this.apkAbsolutePath);
                arguments.putString("packageName", PermissionFragment.this.packageName);
                arguments.putBoolean("deleteAfterFinish", PermissionFragment.this.deleteSwitch.isChecked());
                fragment.setArguments(arguments);
                supportFragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.fragment, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        activity.findViewById(R.id.cancelPermission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (supportFragmentManager.getBackStackEntryCount() != 0)
                    supportFragmentManager.popBackStack();
                else
                    activity.finish();
            }
        });
    }
}
