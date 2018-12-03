package edu_cn.pku.course.adapter;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import edu_cn.pku.course.activities.AnnouncementBodyActivity;
import edu_cn.pku.course.activities.R;
import edu_cn.pku.course.fragments.AnnouncementListFragment;
import edu_cn.pku.course.fragments.AnnouncementListOfEachCourseFragment;

/**
 * Created by zhang on 2016.08.07.
 */
public class AnnouncementListOfEachCourseAdapter extends RecyclerView.Adapter<AnnouncementListOfEachCourseAdapter.RecyclerViewHolder> {

    private ArrayList<AnnouncementListFragment.AnnouncementInfo> announcementList;
    private AnnouncementListOfEachCourseFragment mContext;
    //private SharedPreferences sharedPreferences;

    public AnnouncementListOfEachCourseAdapter(ArrayList<AnnouncementListFragment.AnnouncementInfo> announcementList, AnnouncementListOfEachCourseFragment context) {
        this.announcementList = announcementList;
        Collections.sort(this.announcementList);
        this.mContext = context;
        //this.sharedPreferences = sharedPreferences;//不要排序吧，作为公告
    }

    public void updateList(ArrayList<AnnouncementListFragment.AnnouncementInfo> announcementList) {
        this.announcementList = announcementList;
        Collections.sort(this.announcementList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_announcement_recycler_view, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {
        holder.setIsRecyclable(false);

        // holder.mView.setBackgroundColor(courseColorGet(announcementList.get(holder.getAdapterPosition()).isPinned()));
        holder.recycler_announcement_title_str.setText(announcementList.get(holder.getAdapterPosition()).getAnnouncementTitle());
        holder.recycler_announcement_date_str.setText(announcementList.get(holder.getAdapterPosition()).getAnnouncementDate());

        holder.mView.setClickable(true);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext.getActivity(), AnnouncementBodyActivity.class);
                intent.putExtra("AnnouncementId", announcementList.get(holder.getAdapterPosition()).getAnnouncementId());
                intent.putExtra("Title", announcementList.get(holder.getAdapterPosition()).getAnnouncementTitle());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return announcementList.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView recycler_announcement_title_str, recycler_announcement_date_str;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            recycler_announcement_title_str = itemView.findViewById(R.id.recycler_announcement_title_str);
            recycler_announcement_date_str = itemView.findViewById(R.id.recycler_announcement_date_str);

        }
    }
}