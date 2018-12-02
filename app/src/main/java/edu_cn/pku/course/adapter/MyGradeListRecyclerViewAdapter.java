package edu_cn.pku.course.adapter;

import android.content.Intent;
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

import edu_cn.pku.course.activities.GradeBookOfEachCourseActivity;
import edu_cn.pku.course.activities.R;
import edu_cn.pku.course.fragments.CourseListFragment;
import edu_cn.pku.course.fragments.GradeBookOfEachCourseFragment;
import edu_cn.pku.course.fragments.MyGradeFragment;

// * Created by zhang on 2016.08.07.
public class MyGradeListRecyclerViewAdapter extends RecyclerView.Adapter<MyGradeListRecyclerViewAdapter.RecyclerViewHolder> {

    private ArrayList<MyGradeFragment.CourseInfo> coursesList;
    private SharedPreferences sharedPreferences;
    private MyGradeFragment mContext;

    public MyGradeListRecyclerViewAdapter(ArrayList<MyGradeFragment.CourseInfo> coursesList, SharedPreferences sharedPreferences, MyGradeFragment context) {
        this.coursesList = coursesList;
        Collections.sort(this.coursesList);
        this.sharedPreferences = sharedPreferences;
        this.mContext = context;
    }

    public void updateList(ArrayList<MyGradeFragment.CourseInfo> coursesList) {
        this.coursesList = coursesList;
        Collections.sort(this.coursesList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_courses_recycler_view, parent, false);
        return new RecyclerViewHolder(view);
    }

    private int courseColorGet(int isPinned) {
        return isPinned == 1 ? Color.parseColor("#EEEEEE") : Color.WHITE;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {
        holder.setIsRecyclable(false);

        holder.mView.setBackgroundColor(courseColorGet(coursesList.get(holder.getAdapterPosition()).isPinned()));
        holder.recycler_course_name_str.setText(coursesList.get(holder.getAdapterPosition()).getCourseName());
        holder.recycler_course_semester_str.setText(coursesList.get(holder.getAdapterPosition()).getSemesterString());

        holder.mView.setClickable(true);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext.getActivity(), GradeBookOfEachCourseActivity.class);
                intent.putExtra("CourseId", coursesList.get(holder.getAdapterPosition()).getCourseId());
                intent.putExtra("Title", "成绩—" + coursesList.get(holder.getAdapterPosition()).getCourseName());
                mContext.startActivity(intent);
            }
        });

        holder.mView.setLongClickable(true);
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                coursesList.get(holder.getAdapterPosition()).setPinned(coursesList.get(holder.getAdapterPosition()).isPinned() == 1 ? 0 : 1);
                Collections.sort(coursesList);
                notifyDataSetChanged();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Set<String> set = new HashSet<>();
                for (MyGradeFragment.CourseInfo k : coursesList)
                    if (k.isPinned() == 1)
                        set.add(k.getRawCourseName());
                editor.putStringSet("key", set);
                editor.apply();

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return coursesList.size();
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView recycler_course_name_str, recycler_course_semester_str;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            recycler_course_name_str = itemView.findViewById(R.id.recycler_str);
            recycler_course_semester_str = itemView.findViewById(R.id.recycler_sub_str);
        }
    }
}
