package edu_cn.pku.course.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;

import edu_cn.pku.course.Utils;
import edu_cn.pku.course.activities.AnnouncementListOfEachCourseActivity;
import edu_cn.pku.course.activities.CourseActionsActivity;
import edu_cn.pku.course.activities.GradeBookOfEachCourseActivity;
import edu_cn.pku.course.activities.R;
import edu_cn.pku.course.activities.WebViewActivity;
import edu_cn.pku.course.fragments.CourseActionFragment;

public class CourseActionsAdapter extends RecyclerView.Adapter<CourseActionsAdapter.ActionViewHolder> {
    private CourseActionFragment mContext;
    private ArrayList<String> action_list;

    public CourseActionsAdapter(ArrayList<String> action_list, CourseActionFragment context) {
        this.action_list = action_list;
        this.mContext = context;
    }


    @NonNull
    @Override
    public CourseActionsAdapter.ActionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ActionViewHolder(LayoutInflater.from(mContext.getContext()).inflate(R.layout.item_actions_recycler_view, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CourseActionsAdapter.ActionViewHolder holder, int position) {
        final Node nNode = Utils.stringToNode(action_list.get(position));
        if (nNode != null) {
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                final Element eElement = (Element) nNode;
                holder.textView.setText(eElement.getAttribute("name"));
                if (nNode.getChildNodes().getLength() > 0) {
                    holder.textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext.getActivity(), CourseActionsActivity.class);
                            Activity activity = mContext.getActivity();
                            if (activity != null) {
                                intent.putExtra("CourseId", activity.getIntent().getStringExtra("CourseId"));
                                intent.putExtra("Title", activity.getIntent().getStringExtra("Title") + " - " + eElement.getAttribute("name"));
                            }
                            intent.putExtra("CourseActionsXML", Utils.nodeToString(nNode.getFirstChild()));
                            mContext.startActivity(intent);
                        }
                    });
                    holder.textView.setClickable(true);
                } else if ("student_gradebook".equals(eElement.getAttribute("linktype"))) {
                    holder.textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext.getActivity(), GradeBookOfEachCourseActivity.class);
                            Activity activity = mContext.getActivity();
                            if (activity != null) {
                                intent.putExtra("CourseId", activity.getIntent().getStringExtra("CourseId"));
                            }
                            mContext.startActivity(intent);
                        }
                    });
                } else if ("announcements".equals(eElement.getAttribute("linktype"))) {
                    holder.textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext.getActivity(), AnnouncementListOfEachCourseActivity.class);
                            Activity activity = mContext.getActivity();
                            if (activity != null) {
                                intent.putExtra("Title", activity.getIntent().getStringExtra("Title") + " - " + eElement.getAttribute("name"));
                                intent.putExtra("CourseId", activity.getIntent().getStringExtra("CourseId"));
                            }
                            mContext.startActivity(intent);
                        }
                    });
                } else {
                    holder.textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext.getActivity(), WebViewActivity.class);
                            Activity activity = mContext.getActivity();
                            if (activity != null) {
                                intent.putExtra("Title", activity.getIntent().getStringExtra("Title") + " - " + eElement.getAttribute("name"));
                                intent.putExtra("WebViewUrl", eElement.getAttribute("viewurl"));
                            }
                            mContext.startActivity(intent);
                        }
                    });
                }
            } else {
                holder.textView.setText(Utils.errorPrefix + "nNode.getNodeType() != Node.ELEMENT_NODE");

            }
        }
    }

    @Override
    public int getItemCount() {
        return action_list.size();
    }

    public void updateList(ArrayList<String> actions_list) {
        this.action_list = actions_list;
    }


    class ActionViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        ActionViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.action_recycler_str);
        }
    }
}
