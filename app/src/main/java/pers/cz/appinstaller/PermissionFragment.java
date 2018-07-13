package pers.cz.appinstaller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class PermissionFragment extends android.support.v4.app.Fragment {
    private SwitchCompat switchCompat;
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
        switchCompat = (SwitchCompat) getActivity().findViewById(R.id.deleteAfterFinish);
        final ExpandableListView expandableListView = (ExpandableListView) getActivity().findViewById(R.id.permissionList);
        final String[] classes = new String[]{"包含15条权限"};
        final String[][] students = new String[][]{{"root权限", "root权限", "root权限", "root权限", "root权限", "root权限", "root权限", "root权限"}};
        expandableListView.setAdapter(new MyExpandableListAdapter(classes, students, getActivity(), new View.OnClickListener() {
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
                for (int i = 0; i < classes.length; i++)
                    if (i != groupPosition)
                        expandableListView.collapseGroup(i);
            }
        });
//        expandableListView.expandGroup(0);
        getActivity().findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity()
                        .getSupportFragmentManager();
                fragmentManager.popBackStackImmediate();
                ProgressFragment fragment = new ProgressFragment();
                Bundle arguments = new Bundle();
                arguments.putString("apkAbsolutePath", PermissionFragment.this.apkAbsolutePath);
                arguments.putString("packageName", PermissionFragment.this.packageName);
                arguments.putBoolean("deleteAfterFinish", PermissionFragment.this.switchCompat.isChecked());
                fragment.setArguments(arguments);
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.fragment, fragment)
                        .commit();
            }
        });
        getActivity().findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() != 0)
                    getActivity().getSupportFragmentManager().popBackStack();
                else
                    getActivity().finish();
            }
        });
    }
}
