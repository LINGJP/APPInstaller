package pers.cz.appinstaller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {
    private String[] groups;
    private String[][] children;
    private Context context;
    private View.OnClickListener clickListener;


    class GroupHold {
        TextView groupNameTextView;
        TextView groupExpandButton;
    }

    class ChildHold {
        TextView childNameTextView;
    }

    public MyExpandableListAdapter(String[] groups, String[][] children, Context context, View.OnClickListener clickListener) {
        this.groups = groups;
        this.children = children;
        this.context = context;
        this.clickListener = clickListener;
    }

    @Override
    public int getGroupCount() {
        return groups.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return children[groupPosition].length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return children[groupPosition][childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHold groupHold;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_group, null);
            groupHold = new GroupHold();
            groupHold.groupNameTextView = (TextView) convertView.findViewById(R.id.groupName);
            groupHold.groupExpandButton = (TextView) convertView.findViewById(R.id.groupExpand);
            convertView.setTag(groupHold);
        } else
            groupHold = (GroupHold) convertView.getTag();
        String groupName = groups[groupPosition];
        groupHold.groupNameTextView.setText(groupName);
        //取消默认的groupIndicator后根据方法中传入的isExpand判断组是否展开并动态自定义指示器
        if (isExpanded)
            groupHold.groupExpandButton.setText("∨");
        else
            groupHold.groupExpandButton.setText("＞");
        groupHold.groupExpandButton.setTag(groupPosition);
        //图标的点击事件
        groupHold.groupExpandButton.setOnClickListener(clickListener);
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHold childHold;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_child, null);
            childHold = new ChildHold();
            childHold.childNameTextView = (TextView) convertView.findViewById(R.id.childName);
            convertView.setTag(childHold);
        } else
            childHold = (ChildHold) convertView.getTag();
        childHold.childNameTextView.setText(children[groupPosition][childPosition]);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}