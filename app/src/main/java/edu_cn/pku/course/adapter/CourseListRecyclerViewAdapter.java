package edu_cn.pku.course.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu_cn.pku.course.pkucourse.R;

/**
 * Created by zhang on 2016.08.07.
 */
public class CourseListRecyclerViewAdapter extends RecyclerView.Adapter<CourseListRecyclerViewAdapter.RecyclerViewHolder> {

    private String[] str;

    public CourseListRecyclerViewAdapter(String[] str) {
        this.str = str;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_courses_recycler_view, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {
        holder.setIsRecyclable(false);

        holder.recycler_str.setText(str[holder.getAdapterPosition()]);
        holder.recycler_str.setBackgroundColor(holder.getAdapterPosition() % 2 == 0 ? Color.WHITE : Color.parseColor("#F5F5F5"));

        holder.mView.setLongClickable(true);
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//                if (cmb != null) {
//                    cmb.setText(colorValues[holder.getAdapterPosition()]);
//                }
                Snackbar.make(holder.mView, "Test", Snackbar.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return str.length;
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView recycler_str;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            recycler_str = itemView.findViewById(R.id.recycler_str);
        }
    }
}
