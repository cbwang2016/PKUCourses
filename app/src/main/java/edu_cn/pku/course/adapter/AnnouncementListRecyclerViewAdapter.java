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
import edu_cn.pku.course.fragments.AnnouncementListFragment;

/**
 * Created by zhang on 2016.08.07.
 */
public class AnnouncementListRecyclerViewAdapter extends RecyclerView.Adapter<AnnouncementListRecyclerViewAdapter.RecyclerViewHolder> {

    private ArrayList<AnnouncementListFragment.AnnouncementInfo> announcementList;
    //private SharedPreferences sharedPreferences;

    public AnnouncementListRecyclerViewAdapter(ArrayList<AnnouncementListFragment.AnnouncementInfo> announcementList) {
        this.announcementList = announcementList;
        Collections.sort(this.announcementList);
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

    /**private int courseColorGet(int isPinned) {
        return isPinned == 1 ? Color.parseColor("#EEEEEE") : Color.WHITE;
    }*/

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {
        holder.setIsRecyclable(false);

       // holder.mView.setBackgroundColor(courseColorGet(announcementList.get(holder.getAdapterPosition()).isPinned()));
        holder.recycler_announcement_title_str.setText(announcementList.get(holder.getAdapterPosition()).getAnnouncementTitle());
        holder.recycler_announcement_date_str.setText(announcementList.get(holder.getAdapterPosition()).getAnnouncementDate());

/**
 * 这一段设置长按置顶功能我不知道怎么回事实现不了，那就先算了
        holder.mView.setLongClickable(true);
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                announcementList.get(holder.getAdapterPosition()).setPinned(announcementList.get(holder.getAdapterPosition()).isPinned() == 1 ? 0 : 1);
                Collections.sort(announcementList);
                notifyDataSetChanged();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Set<String> set = new HashSet<>();
                for (AnnouncementListFragment.AnnouncementInfo k : announcementList)
                    if (k.isPinned() == 1)
                        set.add(k.getRawStr());
                editor.putStringSet("key", set);
                editor.apply();

                return true;
            }
        });
 **/
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