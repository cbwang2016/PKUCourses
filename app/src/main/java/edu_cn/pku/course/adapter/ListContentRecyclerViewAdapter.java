package edu_cn.pku.course.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu_cn.pku.course.activities.R;
import edu_cn.pku.course.fragments.ListContentFragment;

/**
 * Created by zhang on 2016.08.07.
 */
// 自定义适配器，将参数传递给item_coursemessage_recycler_view.xml
public class ListContentRecyclerViewAdapter extends RecyclerView.Adapter<ListContentRecyclerViewAdapter.RecyclerViewHolder> {

    private ArrayList<ListContentFragment.ContentInfo> coursemessageList;
    //private SharedPreferences sharedPreferences;

    public ListContentRecyclerViewAdapter(ArrayList<ListContentFragment.ContentInfo> coursemessageList) {
        this.coursemessageList = coursemessageList;
    }

    public void updateList(ArrayList<ListContentFragment.ContentInfo> coursemessageList) {
        this.coursemessageList = coursemessageList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_content_recycler_view, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {
        holder.setIsRecyclable(false);

//        holder.recycler_coursemessage_title_str.setText(coursemessageList.get(holder.getAdapterPosition()).getCourseMessageTitle());
//        holder.recycler_coursemessage_isadd_str.setText(coursemessageList.get(holder.getAdapterPosition()).getCourseMessageIsAdd());
//        holder.recycler_coursemessage_addfiles_str.setText(coursemessageList.get(holder.getAdapterPosition()).getCourseMessageAddFiles());
//        holder.recycler_coursemessage_contents_str.setText(coursemessageList.get(holder.getAdapterPosition()).getCourseMessageContents());
    }

    @Override
    public int getItemCount() {
        return coursemessageList.size();
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private View mView;
//        private TextView recycler_coursemessage_title_str, recycler_coursemessage_isadd_str, recycler_coursemessage_addfiles_str, recycler_coursemessage_contents_str;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
//            recycler_coursemessage_title_str = itemView.findViewById(R.id.recycler_coursemessage_title_str);
//            recycler_coursemessage_isadd_str = itemView.findViewById(R.id.recycler_coursemessage_isadd_str);
//            recycler_coursemessage_addfiles_str = itemView.findViewById(R.id.recycler_coursemessage_addfiles_str);
//            recycler_coursemessage_contents_str = itemView.findViewById(R.id.recycler_coursemessage_contents_str);

        }
    }
}
