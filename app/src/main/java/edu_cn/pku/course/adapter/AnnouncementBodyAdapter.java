package edu_cn.pku.course.adapter;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import edu_cn.pku.course.activities.R;
import edu_cn.pku.course.activities.WebViewActivity;
import edu_cn.pku.course.fragments.AnnouncementBodyFragment;
import edu_cn.pku.course.fragments.AnnouncementListFragment;

public class AnnouncementBodyAdapter extends RecyclerView.Adapter<AnnouncementBodyAdapter.RecyclerViewHolder> {

    private ArrayList<AnnouncementListFragment.AnnouncementInfo> announcementBodyList;
    private AnnouncementBodyFragment mContext;

    public AnnouncementBodyAdapter(ArrayList<AnnouncementListFragment.AnnouncementInfo> announcementBodyList, AnnouncementBodyFragment context) {
        this.announcementBodyList = announcementBodyList;
        mContext = context;
    }

    public void updateList(ArrayList<AnnouncementListFragment.AnnouncementInfo> announcementBodyList) {
        this.announcementBodyList = announcementBodyList;
    }

    @NonNull
    @Override
    public AnnouncementBodyAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_announcement_body_recycler_view, parent, false);
        return new AnnouncementBodyAdapter.RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {
        holder.setIsRecyclable(false);

        holder.announcement_title_str.setText(announcementBodyList.get(holder.getAdapterPosition()).getAnnouncementTitle());
        holder.announcement_date_str.setText(announcementBodyList.get(holder.getAdapterPosition()).getAnnouncementDate());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            holder.announcement_contents.setText(Html.fromHtml(announcementBodyList.get(holder.getAdapterPosition()).getContents(), Html.FROM_HTML_MODE_COMPACT));
            holder.announcement_author.setText(Html.fromHtml(announcementBodyList.get(holder.getAdapterPosition()).getAuthorInfo(), Html.FROM_HTML_MODE_COMPACT));
        } else {
//            holder.announcement_contents.setText(Html.fromHtml(announcementBodyList.get(holder.getAdapterPosition()).getContents()));
            holder.announcement_author.setText(Html.fromHtml(announcementBodyList.get(holder.getAdapterPosition()).getAuthorInfo()));
        }
        setTextViewHTML(holder.announcement_contents, announcementBodyList.get(holder.getAdapterPosition()).getContents());
    }

    private void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(@NonNull View view) {
                String url = span.getURL();
                if (url.startsWith("http://course.pku.edu.cn")) {
                    Intent intent = new Intent(mContext.getActivity(), WebViewActivity.class);
                    Activity activity = mContext.getActivity();
                    if (activity != null) {
                        intent.putExtra("Title", "正在打开链接...");
                        intent.putExtra("WebViewUrl", url.replaceFirst("http://course.pku.edu.cn", ""));
                    }
                    mContext.startActivity(intent);
                } else {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    mContext.startActivity(browserIntent);
                }
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    private void setTextViewHTML(TextView text, String html) {
        CharSequence sequence = Html.fromHtml(html.replaceAll("\n", ""));
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        text.setText(strBuilder);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public int getItemCount() {
        return announcementBodyList.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView announcement_title_str, announcement_date_str, announcement_contents, announcement_author;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            announcement_title_str = itemView.findViewById(R.id.announcement_title_str);
            announcement_date_str = itemView.findViewById(R.id.announcement_date_str);
            announcement_contents = itemView.findViewById(R.id.announcement_contents);
            announcement_author = itemView.findViewById(R.id.announcement_author);
        }
    }

}
