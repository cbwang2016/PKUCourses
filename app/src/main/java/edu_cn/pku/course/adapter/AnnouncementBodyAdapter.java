package edu_cn.pku.course.adapter;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import edu_cn.pku.course.activities.R;
import edu_cn.pku.course.fragments.AnnouncementListFragment;

public class AnnouncementBodyAdapter extends RecyclerView.Adapter<AnnouncementBodyAdapter.RecyclerViewHolder> {

    private ArrayList<AnnouncementListFragment.AnnouncementInfo> announcementBodyList;

    public AnnouncementBodyAdapter(ArrayList<AnnouncementListFragment.AnnouncementInfo> announcementBodyList) {
        this.announcementBodyList = announcementBodyList;
    }

    public void updateList(ArrayList<AnnouncementListFragment.AnnouncementInfo> announcementBodyList) {
        this.announcementBodyList = announcementBodyList;
    }

    @NonNull
    @Override
    public AnnouncementBodyAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_announcement_body_recycler_view, parent, false);
        return new AnnouncementBodyAdapter.RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {
        holder.setIsRecyclable(false);

        holder.announcement_title_str.setText(announcementBodyList.get(holder.getAdapterPosition()).getAnnouncementTitle());
        holder.announcement_date_str.setText(announcementBodyList.get(holder.getAdapterPosition()).getAnnouncementDate());
        holder.announcement_contents.setText(announcementBodyList.get(holder.getAdapterPosition()).getContents());
        holder.announcement_author.setText(announcementBodyList.get(holder.getAdapterPosition()).getAuthorInfo());
        //看不懂下面的东西，等wcb来改一下....好像是用来防止空白公告的？
        /**if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
         holder.recycler_announcement_contents.setText(Html.fromHtml(announcementList.get(holder.getAdapterPosition()).getContents(), Html.FROM_HTML_MODE_COMPACT));
         holder.recycler_announcement_author.setText(Html.fromHtml(announcementList.get(holder.getAdapterPosition()).getAuthorInfo(), Html.FROM_HTML_MODE_COMPACT));
         } else {
         holder.recycler_announcement_contents.setText(Html.fromHtml(announcementList.get(holder.getAdapterPosition()).getContents()));
         holder.recycler_announcement_author.setText(Html.fromHtml(announcementList.get(holder.getAdapterPosition()).getAuthorInfo()));
         }**/
    }

    @Override
    public int getItemCount() {
        return announcementBodyList.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView announcement_title_str, announcement_date_str, announcement_contents, announcement_author;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            announcement_title_str = itemView.findViewById(R.id.announcement_title_str);
            announcement_date_str = itemView.findViewById(R.id.announcement_date_str);
            announcement_contents = itemView.findViewById(R.id.announcement_contents);
            announcement_author = itemView.findViewById(R.id.announcement_author);
        }
    }

}
