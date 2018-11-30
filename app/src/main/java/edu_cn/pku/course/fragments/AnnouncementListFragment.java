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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import edu_cn.pku.course.Utils;
import edu_cn.pku.course.activities.LoginActivity;
import edu_cn.pku.course.activities.R;
import edu_cn.pku.course.adapter.AnnouncementListRecyclerViewAdapter;


public class AnnouncementListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private AnnouncementLoadingTask mLoadingTask = null;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mAnnouncementListSwipeContainer;
    private AnnouncementListRecyclerViewAdapter adapter;

    public AnnouncementListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     *
     * @return A new instance of fragment CoursesListFragment.
     */
    public static AnnouncementListFragment newInstance() {
        AnnouncementListFragment fragment = new AnnouncementListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_announcement_list, container, false);
        // 查找xml文件中的对象并保存进Java变量
        mRecyclerView = linearLayout.findViewById(R.id.recycler_announcement_list);
        mAnnouncementListSwipeContainer = linearLayout.findViewById(R.id.announcement_swipe_container);

        // 设置动画
//        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);
//        mAnnouncementListSwipeContainer.setLayoutAnimation(animation);
        // 设置刷新的监听类为此类（监听函数onRefresh）
        mAnnouncementListSwipeContainer.setOnRefreshListener(this);

        FragmentActivity fa = getActivity();
        // 为了消除编译器Warning，需要判断一下是不是null，其实这基本上不可能出现null
        if (fa == null) {
            return linearLayout;
        }
        // 将读取已置顶课程列表的SharedPreferences传递给CourseListRecyclerViewAdapter
        // SharedPreferences sharedPreferences = fa.getSharedPreferences("pinnedAnnouncementList", Context.MODE_PRIVATE);
        adapter = new AnnouncementListRecyclerViewAdapter(new ArrayList<AnnouncementInfo>());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);

        // 显示Loading的小动画，并在后台读取课程列表
        showLoading(true);
        mLoadingTask = new AnnouncementLoadingTask();
        mLoadingTask.execute((Void) null);

        return linearLayout;
    }


    @Override
    public void onRefresh() {
        if (mLoadingTask == null) {
            mLoadingTask = new AnnouncementLoadingTask();
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

        mAnnouncementListSwipeContainer.setRefreshing(show);
    }

    @SuppressLint("StaticFieldLeak")
    private class AnnouncementLoadingTask extends AsyncTask<Void, Void, String> {

        AnnouncementLoadingTask() {
        }

        //主要是有个course id一串数字是不一样的
        @Override
        protected String doInBackground(Void... params) {
            return Utils.courseHttpGetRequest("http://course.pku.edu.cn/webapps/blackboard/execute/announcement?method=search&returnUrl=/webapps/portal/execute/tabs/tabAction?tab_tab_group_id=_3_1&tabId=_1_1");
        }

        @Override
        protected void onPostExecute(final String str) {

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
                // 解析返回的HTML
                ArrayList<AnnouncementInfo> announcement_list = new ArrayList<>();

                FragmentActivity fa = getActivity();
                if (fa == null) {
                    return;
                }

                Element list = Jsoup.parse(str).getElementById("announcementList");
                Elements nList = list.children();

                for (int temp = 0; temp < nList.size(); temp++) {
                    Element n = nList.get(temp);
                    announcement_list.add(new AnnouncementInfo(n));
                }

                adapter.updateList(announcement_list);
            }
            mLoadingTask = null;
            showLoading(false);
        }

        // 没什么用的函数
        @Override
        protected void onCancelled() {
            mLoadingTask = null;
            showLoading(false);
        }
    }

    //复制wcb的courselist代码

    /**
     * 为了方便管理课程列表，将每个课程的各种信息组成一个类。
     */
    public static class AnnouncementInfo implements Comparable<AnnouncementInfo> {
        private Element nNode;

        /**
         * 格式："> 线性代数B第四次作业</h3>
         * <div class="details">
         * <p><span>发帖时间:</span> 2018年11月15日 星期四</p>
         * <p>
         */
        // private int isPinned;

        AnnouncementInfo(Element nNode) {
            this.nNode = nNode;
        }

        public String getAuthorInfo() {
            return nNode.getElementsByClass("announcementInfo").first().toString();
        }

        public String getContents() {
            Element tmp = nNode.getElementsByClass("details").first().getElementsByClass("vtbegenerated").first();
            return tmp == null ? "" : tmp.toString();
        }

        public String getAnnouncementTitle() {
            return nNode.child(0).text();
        }

        public String getAnnouncementDate() {
            return nNode.getElementsByClass("details").first().child(0).text().replace("发帖时间: ", "");
        }

        //给出可以比较的Date类型
        private Date changeToDate() throws ParseException {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
            return dateFormat.parse(getAnnouncementDate().split(" ")[0]);
        }

        @Override
        public int compareTo(AnnouncementListFragment.AnnouncementInfo comp) {
            try {
//                if (this.changeToDate() == comp.changeToDate())
                return comp.changeToDate().compareTo(this.changeToDate());
            } catch (ParseException e) {
//                e.printStackTrace();//这个是字符串不符合定义的格式的错误
            }
            return this.getAnnouncementTitle().compareTo(comp.getAnnouncementTitle());
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

