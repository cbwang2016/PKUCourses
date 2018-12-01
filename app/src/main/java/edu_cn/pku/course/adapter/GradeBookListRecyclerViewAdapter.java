package edu_cn.pku.course.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import edu_cn.pku.course.activities.R;
import edu_cn.pku.course.fragments.GradeBookOfEachCourseFragment;

public class GradeBookListRecyclerViewAdapter extends RecyclerView.Adapter<GradeBookListRecyclerViewAdapter.RecyclerViewHolder> {

    private ArrayList<GradeBookOfEachCourseFragment.GradeInfo> gradeBookList;


    public GradeBookListRecyclerViewAdapter(ArrayList<GradeBookOfEachCourseFragment.GradeInfo> gradeBookList) {
        this.gradeBookList = gradeBookList;
    }

    //更新列表
    public void updateList(ArrayList<GradeBookOfEachCourseFragment.GradeInfo> gradeBookList) {
        this.gradeBookList = gradeBookList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grade_recycler_view, parent, false);
        return new RecyclerViewHolder(view);
    }

    /**
     * private int courseColorGet(int isPinned) {
     * return isPinned == 1 ? Color.parseColor("#EEEEEE") : Color.WHITE;
     * }
     */

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {
        holder.setIsRecyclable(false);

        // holder.mView.setBackgroundColor(courseColorGet(announcementList.get(holder.getAdapterPosition()).isPinned()));
        holder.grade_title_str.setText(gradeBookList.get(holder.getAdapterPosition()).getGradeTitle());
        holder.grade_description_str.setText(gradeBookList.get(holder.getAdapterPosition()).getGradeDescription());
        holder.grade_str.setText(gradeBookList.get(holder.getAdapterPosition()).getGrade());
        holder.detailed_grade_str.setText(gradeBookList.get(holder.getAdapterPosition()).getDetailedGrade());

    }

    @Override
    public int getItemCount() {
        return gradeBookList.size();
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView grade_title_str, grade_description_str;
        private TextView grade_str, detailed_grade_str;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            grade_title_str = itemView.findViewById(R.id.grade_title_str);
            grade_description_str = itemView.findViewById(R.id.grade_description_str);
            grade_str = itemView.findViewById(R.id.grade_str);
            detailed_grade_str = itemView.findViewById(R.id.detailed_grade_str);
        }
    }
}
