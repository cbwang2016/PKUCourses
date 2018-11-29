package edu_cn.pku.course.adapter;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import edu_cn.pku.course.activities.R;
import edu_cn.pku.course.fragments.DashboardFragment;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder> {

    private DashboardFragment mContext;
    private ArrayList<DashboardFragment.DashboardItem> item_list;

    public DashboardAdapter(ArrayList<DashboardFragment.DashboardItem> action_list, DashboardFragment context) {
        this.item_list = action_list;
        this.mContext = context;
    }

    @NonNull
    @Override
    public DashboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new DashboardViewHolder(LayoutInflater.from(mContext.getContext()).inflate(R.layout.item_dashboard, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(@NonNull DashboardViewHolder holder, int i) {
        holder.recycler_title.setText(item_list.get(holder.getAdapterPosition()).getTitle());
        holder.recycler_sub_str.setText(item_list.get(holder.getAdapterPosition()).getCourseName());
        holder.recycler_time_str.setText(item_list.get(holder.getAdapterPosition()).getRelativeTime());
        holder.card_view_item_dashboard.setClickable(true);
        if (item_list.get(holder.getAdapterPosition()).getSourceId().contains("GradeDetail")) {
            holder.imageView.setBackground(mContext.getResources().getDrawable(R.drawable.ic_mygrade));
        }
    }

    @Override
    public int getItemCount() {
        return item_list.size();
    }

    public void updateList(ArrayList<DashboardFragment.DashboardItem> item_list) {
        this.item_list = item_list;
        Collections.sort(item_list);
        notifyDataSetChanged();
    }

    class DashboardViewHolder extends RecyclerView.ViewHolder {

        private CardView card_view_item_dashboard;
        ImageView imageView;
        private TextView recycler_title, recycler_sub_str, recycler_time_str;

        DashboardViewHolder(@NonNull View itemView) {
            super(itemView);
            recycler_title = itemView.findViewById(R.id.recycler_str);
            recycler_sub_str = itemView.findViewById(R.id.recycler_sub_str);
            recycler_time_str = itemView.findViewById(R.id.recycler_time_str);
            card_view_item_dashboard = itemView.findViewById(R.id.card_view_item_dashboard);
            imageView = itemView.findViewById(R.id.rela_round);
        }
    }
}
