package edu_cn.pku.course.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
//import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
//import java.util.zip.Inflater;
import edu_cn.pku.course.activities.AboutActivity;
//import edu_cn.pku.course.activities.About;
import edu_cn.pku.course.activities.R;

public class AboutAdapter extends ArrayAdapter<AboutActivity.About> {

    public AboutAdapter(AboutActivity context, int item_about_list_view, List<AboutActivity.About> about) {
        super(context, item_about_list_view, about);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent){

        // 获取about数据
        AboutActivity.About about = getItem(position);

        // 创建布局
        @SuppressLint("ViewHolder")
        View oneAboutView = LayoutInflater.from(getContext()).inflate(R.layout.activity_about, parent, false);

        // 获取item_about
        TextView textView = oneAboutView.findViewById(R.id.item_about);

        assert about != null;
        textView.setText(about.getMenu());

        return oneAboutView;
    }
}
