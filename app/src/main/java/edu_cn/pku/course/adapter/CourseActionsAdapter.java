package edu_cn.pku.course.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import edu_cn.pku.course.activities.R;

public class CourseActionsAdapter extends RecyclerView.Adapter<CourseActionsAdapter.ActionViewHolder> {
    private Context mContext;
    private ArrayList<String> action_list;

    public CourseActionsAdapter(ArrayList<String> action_list, Context context) {
        this.action_list = action_list;
        this.mContext = context;
    }


    @NonNull
    @Override
    public CourseActionsAdapter.ActionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ActionViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_actions_recycler_view, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CourseActionsAdapter.ActionViewHolder holder, int position) {
        Node nNode = Utils.stringToNode(action_list.get(position));
        if (nNode != null) {
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                holder.textView.setText(eElement.getAttribute("name"));
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
