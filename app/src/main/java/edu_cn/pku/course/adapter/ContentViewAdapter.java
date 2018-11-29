package edu_cn.pku.course.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import edu_cn.pku.course.activities.ContentViewActivity;
import edu_cn.pku.course.activities.R;

public class ContentViewAdapter extends RecyclerView.Adapter<ContentViewAdapter.ContentViewItemHolder> {

    private Context mContext;
    private ArrayList<ContentViewActivity.AttachedFileItem> list;

    public ContentViewAdapter(ArrayList<ContentViewActivity.AttachedFileItem> list, ContentViewActivity context) {
        this.list = list;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ContentViewItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new ContentViewItemHolder(LayoutInflater.from(mContext).inflate(R.layout.item_view_content_attached_file, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewItemHolder holder, int i) {
        holder.content_view_file_name.setText(list.get(holder.getAdapterPosition()).getTitle());
        holder.content_view_file_size.setText(list.get(holder.getAdapterPosition()).getFileSize());

        holder.card_view_item_content.setClickable(true);
    }

    public void updateList(ArrayList<ContentViewActivity.AttachedFileItem> item_list) {
        this.list = item_list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ContentViewItemHolder extends RecyclerView.ViewHolder {

        private CardView card_view_item_content;
        ImageView content_view_icon;
        private TextView content_view_file_name, content_view_file_size;

        ContentViewItemHolder(@NonNull View itemView) {
            super(itemView);
            content_view_file_name = itemView.findViewById(R.id.content_view_file_name);
            content_view_file_size = itemView.findViewById(R.id.content_view_file_size);
            card_view_item_content = itemView.findViewById(R.id.card_view_item_content);
            content_view_icon = itemView.findViewById(R.id.content_view_icon);
        }
    }
}
