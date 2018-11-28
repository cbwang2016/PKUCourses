package edu_cn.pku.course.adapter;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import edu_cn.pku.course.activities.R;
import edu_cn.pku.course.fragments.CourseMessageListFragment;

/**
 * Created by zhang on 2016.08.07.
 */
// 自定义适配器，将参数传递给item_coursemessage_recycler_view.xml
public class CourseMessageListRecyclerViewAdapter extends RecyclerView.Adapter<CourseMessageListRecyclerViewAdapter.RecyclerViewHolder> {

    private ArrayList<CourseMessageListFragment.CourseMessageInfo> coursemessageList;
    //private SharedPreferences sharedPreferences;

    public CourseMessageListRecyclerViewAdapter(ArrayList<CourseMessageListFragment.CourseMessageInfo> coursemessageList) {
        this.coursemessageList = coursemessageList;
    }

    public void updateList(ArrayList<CourseMessageListFragment.CourseMessageInfo> coursemessageList) {
        this.coursemessageList = coursemessageList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coursemessage_recycler_view, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {
        holder.setIsRecyclable(false);

        holder.recycler_coursemessage_title_str.setText(coursemessageList.get(holder.getAdapterPosition()).getCourseMessageTitle());
        holder.recycler_coursemessage_isadd_str.setText(coursemessageList.get(holder.getAdapterPosition()).getCourseMessageIsAdd());
        holder.recycler_coursemessage_addfiles_str.setText(coursemessageList.get(holder.getAdapterPosition()).getCourseMessageAddFiles());
        holder.recycler_coursemessage_contents_str.setText(coursemessageList.get(holder.getAdapterPosition()).getCourseMessageContents());
    }

    @Override
    public int getItemCount() {
        return coursemessageList.size();
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView recycler_coursemessage_title_str, recycler_coursemessage_isadd_str, recycler_coursemessage_addfiles_str, recycler_coursemessage_contents_str;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            recycler_coursemessage_title_str = itemView.findViewById(R.id.recycler_coursemessage_title_str);
            recycler_coursemessage_isadd_str = itemView.findViewById(R.id.recycler_coursemessage_isadd_str);
            recycler_coursemessage_addfiles_str = itemView.findViewById(R.id.recycler_coursemessage_addfiles_str);
            recycler_coursemessage_contents_str = itemView.findViewById(R.id.recycler_coursemessage_contents_str);

        }
    }
}
