package edu_cn.pku.course.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import edu_cn.pku.course.Utils;
import edu_cn.pku.course.pkucourse.R;

/**
 * Created by zhang on 2016.08.07.
 */
public class CourseListRecyclerViewAdapter extends RecyclerView.Adapter<CourseListRecyclerViewAdapter.RecyclerViewHolder> {

    private ArrayList<String> coursesStrs;
    private ArrayList<String> pinnedCoursesStrs;

    public CourseListRecyclerViewAdapter(ArrayList<String> coursesStrs, ArrayList<String> pinnedCoursesStrs) {
        this.coursesStrs = coursesStrs;
        Collections.sort(this.coursesStrs);
        this.pinnedCoursesStrs = pinnedCoursesStrs;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_courses_recycler_view, parent, false);
        return new RecyclerViewHolder(view);
    }

    private String courseStringGet(int index) {
        return index < pinnedCoursesStrs.size() ? pinnedCoursesStrs.get(index) : coursesStrs.get(index - pinnedCoursesStrs.size());
    }

    private int courseColorGet(int index) {
        if (index < pinnedCoursesStrs.size()) {
            return Color.parseColor("#EEEEEE");
//            return index % 2 == 0 ? Color.parseColor("#B3E5FC") : Color.parseColor("#81D4FA");
        } else {
//            return index % 2 == 0 ? Color.WHITE : Color.parseColor("#F5F5F5");
            return Color.WHITE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {
        holder.setIsRecyclable(false);

        holder.mView.setBackgroundColor(courseColorGet(holder.getAdapterPosition()));
        holder.recycler_str.setText(courseStringGet(holder.getAdapterPosition()).split("\\([0-9]")[0]);
        holder.recycler_sub_str.setText(Utils.lastBetweenStrings(courseStringGet(holder.getAdapterPosition()), "(", ")"));

        holder.mView.setLongClickable(true);
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (holder.getAdapterPosition() < pinnedCoursesStrs.size()) {
                    String tmp = courseStringGet(holder.getAdapterPosition());
                    coursesStrs.add(tmp);
                    Collections.sort(coursesStrs);
                    pinnedCoursesStrs.remove(holder.getAdapterPosition());
                } else {
                    pinnedCoursesStrs.add(0, courseStringGet(holder.getAdapterPosition()));
                    coursesStrs.remove(holder.getAdapterPosition() - pinnedCoursesStrs.size() + 1);
                }
                notifyDataSetChanged();
                Snackbar.make(holder.mView, "TODO: 置顶【" + holder.recycler_str.getText() + "】", Snackbar.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return coursesStrs.size() + pinnedCoursesStrs.size();
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView recycler_str, recycler_sub_str;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            recycler_str = itemView.findViewById(R.id.recycler_str);
            recycler_sub_str = itemView.findViewById(R.id.recycler_sub_str);
        }
    }
}
