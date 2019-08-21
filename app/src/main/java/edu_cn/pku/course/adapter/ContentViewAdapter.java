package edu_cn.pku.course.adapter;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import edu_cn.pku.course.Utils;
import edu_cn.pku.course.activities.ContentViewActivity;
import edu_cn.pku.course.activities.R;
import pub.devrel.easypermissions.EasyPermissions;

public class ContentViewAdapter extends RecyclerView.Adapter<ContentViewAdapter.ContentViewItemHolder> {

    private ContentViewActivity mContext;
    private ArrayList<ContentViewActivity.AttachedFileItem> list;
    private String folder;
    public ContentViewAdapter(ArrayList<ContentViewActivity.AttachedFileItem> list, ContentViewActivity context) {
        this.list = list;
        this.mContext = context;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("login_info", Context.MODE_PRIVATE);
        folder = sharedPreferences.getString("path_preference", null);
    }

    @NonNull
    @Override
    public ContentViewItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new ContentViewItemHolder(LayoutInflater.from(mContext).inflate(R.layout.item_view_content_attached_file, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ContentViewItemHolder holder, int i) {
        holder.content_view_file_name.setText(list.get(holder.getAdapterPosition()).getFileName());
        holder.content_view_file_size.setText(list.get(holder.getAdapterPosition()).getFileSize());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Drawable tmp = mContext.getIcon(list.get(holder.getAdapterPosition()).getFileName());
            if (tmp != null)
                holder.content_view_icon.setBackground(tmp);
        }

        holder.card_view_item_content.setClickable(true);
        holder.card_view_item_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isSDCardPresent()) {
                    mContext.startDownload(list.get(holder.getAdapterPosition()));
                } else {
                    Toast.makeText(mContext, "SD Card not found", Toast.LENGTH_LONG).show();
                }
            }
        });

        if (list.get(holder.getAdapterPosition()).isDownloaded()) {
            System.out.println("is Downloaded!!!");
            holder.card_view_item_content.setCardBackgroundColor(Color.parseColor("#4CAF50"));
            holder.card_view_item_content.setLongClickable(true);
            holder.card_view_item_content.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(mContext, R.style.AlertDialogTheme)
                            .setMessage("您是否想删除此文件（" + list.get(holder.getAdapterPosition()).getFileName() + "）的本地缓存？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    boolean tmp = new File(folder + list.get(holder.getAdapterPosition()).getFileName()).delete();
                                    if (!tmp) {
                                        new AlertDialog.Builder(mContext)
                                                .setMessage("文件删除失败")
                                                .setPositiveButton("确定", null).show();
                                    }
                                    list.get(holder.getAdapterPosition()).setDownloaded(false);
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("取消", null).show();
                    return true;
                }
            });
        } else {
            holder.card_view_item_content.setCardBackgroundColor(Color.WHITE);
            holder.card_view_item_content.setLongClickable(false);
        }
    }

    public void refreshList() {
        if (EasyPermissions.hasPermissions(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            for (ContentViewActivity.AttachedFileItem tmp : this.list) {
                String filePath = folder + tmp.getFileName();
                if (new File(filePath).exists()) {
                    tmp.setDownloaded(true);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateList(ArrayList<ContentViewActivity.AttachedFileItem> item_list) {
        this.list = item_list;
        if (EasyPermissions.hasPermissions(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            for (ContentViewActivity.AttachedFileItem tmp : this.list) {
                String filePath = folder + tmp.getFileName();
                if (new File(filePath).exists()) {
                    tmp.setDownloaded(true);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ContentViewItemHolder extends RecyclerView.ViewHolder {

        private CardView card_view_item_content;
        private ImageView content_view_icon;
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
