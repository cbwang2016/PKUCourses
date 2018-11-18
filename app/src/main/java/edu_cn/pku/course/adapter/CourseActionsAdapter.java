package edu_cn.pku.course.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu_cn.pku.course.activities.R;

public class CourseActionsAdapter extends RecyclerView.Adapter<CourseActionsAdapter.ActionViewHolder> {
    private Context mContext;
    private List<String> action_list = new ArrayList<String>(){{
        add("新增内容");
        add("公告/通知");
        add("信息");
        add("内容");
        add("我的成绩");
        add("其他");
    }};

    public CourseActionsAdapter(Context context){
        this.mContext=context;
    }


    @NonNull
    @Override
    public CourseActionsAdapter.ActionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ActionViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_actions_recycler_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CourseActionsAdapter.ActionViewHolder holder, int position) {

        holder.textView.setText(action_list.get(position));
    }

    @Override
    public int getItemCount() {
        return action_list.size();
    }


    class ActionViewHolder extends RecyclerView.ViewHolder{

        private TextView textView;

        public ActionViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.action_recycler_str);
        }
    }
}
