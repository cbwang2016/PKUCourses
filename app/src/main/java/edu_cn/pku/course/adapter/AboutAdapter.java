package edu_cn.pku.course.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu_cn.pku.course.activities.AboutActivity;
import edu_cn.pku.course.activities.R;

public class AboutAdapter extends BaseAdapter {
    private Context mContext;
    private List<AboutActivity.AboutMenu> mList = new ArrayList<>();

    public AboutAdapter(Context context, List<AboutActivity.AboutMenu> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_about_list_view, null);
            viewHolder.mMenuTextView = (TextView) view.findViewById(R.id.item_about_text);
            viewHolder.mImageView = (ImageView) view.findViewById(R.id.item_about_image);
            viewHolder.mContentTextView = (TextView)view.findViewById(R.id.item_about_content_text);
            viewHolder.mSubMenuTextView = (TextView)view.findViewById(R.id.item_about_sub_menu_text);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.mMenuTextView.setText(mList.get(i).getMenu());
        viewHolder.mImageView.setImageResource(mList.get(i).getImageId());
        viewHolder.mContentTextView.setText(mList.get(i).getContent());
        viewHolder.mSubMenuTextView.setText(mList.get(i).getSubMenu());
        return view;
    }
    class ViewHolder {
        TextView mMenuTextView;
        TextView mSubMenuTextView;
        TextView mContentTextView;
        ImageView mImageView;
    }
}
