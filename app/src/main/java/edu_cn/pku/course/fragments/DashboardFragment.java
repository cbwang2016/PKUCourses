package edu_cn.pku.course.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import edu_cn.pku.course.Utils;
import edu_cn.pku.course.activities.LoginActivity;
import edu_cn.pku.course.activities.R;
import edu_cn.pku.course.adapter.DashboardAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    DashboardLoadingTask mLoadingTask = null;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeContainer;
    private DashboardAdapter adapter;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_dashboard, container, false);
        // 查找xml文件中的对象并保存进Java变量
        mRecyclerView = linearLayout.findViewById(R.id.recycler_dashboard);
        mSwipeContainer = linearLayout.findViewById(R.id.dashboard_swipe_container);

        adapter = new DashboardAdapter(new ArrayList<DashboardItem>(), this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);

        mSwipeContainer.setOnRefreshListener(this);

        FragmentActivity fa = getActivity();
        if (fa != null) {

            showLoading(true);
            mLoadingTask = new DashboardLoadingTask();
            mLoadingTask.execute((Void) null);
        }

        return linearLayout;
    }

    @Override
    public void onRefresh() {
        if (mLoadingTask == null) {
            mLoadingTask = new DashboardLoadingTask();
            mLoadingTask.execute((Void) null);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showLoading(final boolean show) {
        // 逐渐显示mRecyclerView的小动画
        int shortAnimTime = 200;

        mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        mRecyclerView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mSwipeContainer.setRefreshing(show);
    }

    @SuppressLint("StaticFieldLeak")
    private class DashboardLoadingTask extends AsyncTask<Void, Void, String> {
        DashboardLoadingTask() {
        }

        @Override
        protected String doInBackground(Void... params) {
            return Utils.courseHttpGetRequest("http://course.pku.edu.cn/webapps/Bb-mobile-bb_bb60/dashboard?course_type=ALL&with_notifications=true");
        }

        @Override
        protected void onPostExecute(final String str) {
            mLoadingTask = null;
            showLoading(false);

            // 出现了错误
            if (str.startsWith(Utils.errorPrefix)) {
                if (str.equals(Utils.errorPrefix + Utils.errorPasswordIncorrect)) {
                    // 密码错误
                    try {
                        signOut();
                    } catch (Exception e) {
                        Snackbar.make(mRecyclerView, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    // 其他网络错误
                    Snackbar.make(mRecyclerView, str, Snackbar.LENGTH_SHORT).show();
                }
            } else {

                FragmentActivity fa = getActivity();
                if (fa == null) {
                    return;
                }
                Node rootNode = Utils.stringToNode(str);
                if (rootNode != null) {
                    ArrayList<DashboardItem> item_list = new ArrayList<>();
                    NodeList nList = rootNode.getLastChild().getChildNodes();
                    NodeList nCoursesList = rootNode.getFirstChild().getChildNodes();
                    for (int temp = 0; temp < nList.getLength(); temp++) {
                        Element feed_item = (Element) nList.item(temp);
                        if (feed_item.hasAttribute("contentid") || feed_item.getAttribute("type").equals("ANNOUNCEMENT")) {
                            for (int temp2 = 0; temp2 < nCoursesList.getLength(); temp2++) {
                                Element course_item = (Element) nCoursesList.item(temp2);
                                if (course_item.getAttribute("bbid").equals(feed_item.getAttribute("courseid")))
                                    feed_item.setAttribute("courseName", course_item.getAttribute("name").split("\\([0-9]")[0]);
                            }
                            item_list.add(new DashboardItem(feed_item));
                        }
                    }

                    adapter.updateList(item_list);
                    // 显示课程列表的fancy的动画
                    mRecyclerView.scheduleLayoutAnimation();
                }
            }
        }

        // 没什么用的函数
        @Override
        protected void onCancelled() {
            mLoadingTask = null;
            showLoading(false);
        }
    }

    private static class RelativeDateFormat {

        private static final long ONE_MINUTE = 60000L;
        private static final long ONE_HOUR = 3600000L;
        private static final long ONE_DAY = 86400000L;
        private static final long ONE_WEEK = 604800000L;

        private static final String ONE_SECOND_AGO = "秒前";
        private static final String ONE_MINUTE_AGO = "分钟前";
        private static final String ONE_HOUR_AGO = "小时前";
        private static final String ONE_DAY_AGO = "天前";
        private static final String ONE_MONTH_AGO = "月前";
        private static final String ONE_YEAR_AGO = "年前";

        static String format(Date date) {
            long delta = new Date().getTime() - date.getTime();
            if (delta < ONE_MINUTE) {
                long seconds = toSeconds(delta);
                return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
            }
            if (delta < 45L * ONE_MINUTE) {
                long minutes = toMinutes(delta);
                return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
            }
            if (delta < 24L * ONE_HOUR) {
                long hours = toHours(delta);
                return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
            }
            if (delta < 48L * ONE_HOUR) {
                return "昨天";
            }
            if (delta < 30L * ONE_DAY) {
                long days = toDays(delta);
                return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
            }
            if (delta < 12L * 4L * ONE_WEEK) {
                long months = toMonths(delta);
                return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;
            } else {
                long years = toYears(delta);
                return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;
            }
        }

        private static long toSeconds(long date) {
            return date / 1000L;
        }

        private static long toMinutes(long date) {
            return toSeconds(date) / 60L;
        }

        private static long toHours(long date) {
            return toMinutes(date) / 60L;
        }

        private static long toDays(long date) {
            return toHours(date) / 24L;
        }

        private static long toMonths(long date) {
            return toDays(date) / 30L;
        }

        private static long toYears(long date) {
            return toMonths(date) / 365L;
        }
    }

    public class DashboardItem implements Comparable<DashboardItem> {
        Element n;

        DashboardItem(Element n) {
            this.n = n;
        }

        public String getTitle() {
            return n.getAttribute("message");
        }

        public String getCourseName() {
            return n.getAttribute("courseName");
        }

        public String getItemId() {
            return n.getAttribute("itemid");
        }

        public String getCourseId() {
            return n.getAttribute("courseid");
        }

        public String getContentId() {
            return n.getAttribute("contentid");
        }

        public String getSourceId() {
            return n.getAttribute("sourceid");
        }

        public String getType() {
            return n.getAttribute("type");
        }

        public String getRelativeTime() {
            try {
                return RelativeDateFormat.format(getDate());
            } catch (ParseException e) {
                return Utils.errorPrefix + "Error while calculating time difference";
            }
        }

        private Date getDate() throws ParseException {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz", Locale.US);
            return dateFormat.parse(n.getAttribute("startdate"));
        }

        @Override
        public int compareTo(DashboardItem o) {
            try {
                return o.getDate().compareTo(this.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    public void signOut() throws Exception {
        FragmentActivity fa = getActivity();
        if (fa == null) {
            throw new Exception("Unknown Error: Null getActivity()!");
        }
        SharedPreferences sharedPreferences = fa.getSharedPreferences("login_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
