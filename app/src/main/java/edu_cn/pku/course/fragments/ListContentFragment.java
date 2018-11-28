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
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;

import java.util.ArrayList;

import edu_cn.pku.course.Utils;
import edu_cn.pku.course.activities.LoginActivity;
import edu_cn.pku.course.activities.R;
import edu_cn.pku.course.adapter.ListContentRecyclerViewAdapter;


public class ListContentFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private CourseMessageLoadingTask mLoadingTask = null;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mCourseMessageListSwipeContainer;
    private ListContentRecyclerViewAdapter adapter;

    public ListContentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     *
     * @return A new instance of fragment CoursesListFragment.
     */
    public static ListContentFragment newInstance() {
        ListContentFragment fragment = new ListContentFragment();
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
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_list_content, container, false);
        // 查找xml文件中的对象并保存进Java变量
        mRecyclerView = linearLayout.findViewById(R.id.recycler_coursemessage_list);
        mCourseMessageListSwipeContainer = linearLayout.findViewById(R.id.coursemessage_swipe_container);

        // 设置动画
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);
        mCourseMessageListSwipeContainer.setLayoutAnimation(animation);
        // 设置刷新的监听类为此类（监听函数onRefresh）
        mCourseMessageListSwipeContainer.setOnRefreshListener(this);

        FragmentActivity fa = getActivity();
        // 为了消除编译器Warning，需要判断一下是不是null，其实这基本上不可能出现null
        if (fa == null) {
            Snackbar.make(mRecyclerView, "null getActivity!", Snackbar.LENGTH_SHORT).show();
            return linearLayout;
        }
        // 将读取已置顶课程列表的SharedPreferences传递给CourseListRecyclerViewAdapter
        // SharedPreferences sharedPreferences = fa.getSharedPreferences("pinnedCourseMessageList", Context.MODE_PRIVATE);
        adapter = new ListContentRecyclerViewAdapter(new ArrayList<ContentInfo>());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);

        // 显示Loading的小动画，并在后台读取课程列表
        showLoading(true);
        mLoadingTask = new CourseMessageLoadingTask();
        mLoadingTask.execute((Void) null);

        return linearLayout;
    }

    // 刷新
    @Override
    public void onRefresh() {
        if (mLoadingTask == null) {
            mLoadingTask = new CourseMessageLoadingTask();
            mLoadingTask.execute((Void) null);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showLoading(final boolean show) {
        // 逐渐显示mRecyclerView的小动画
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        mRecyclerView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mCourseMessageListSwipeContainer.setRefreshing(show);
    }

    @SuppressLint("StaticFieldLeak")
    private class CourseMessageLoadingTask extends AsyncTask<Void, Void, String> {

        CourseMessageLoadingTask() {
        }

        //主要是有个course id一串数字是不一样的
        @Override
        protected String doInBackground(Void... params) {
            return Utils.courseHttpGetRequest("http://course.pku.edu.cn/webapps/blackboard/content/listContent.jsp?course_id=_44949_1&content_id=_476828_1&mode=reset");
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
                // 解析返回的HTML
                String[] rawSplit = str.split("<div class=\"item clearfix\"");
                ArrayList<ContentInfo> coursemessage_list = new ArrayList<>();

                FragmentActivity fa = getActivity();
                if (fa == null) {
                    Snackbar.make(mRecyclerView, "null getActivity!", Snackbar.LENGTH_SHORT).show();
                    return;
                }
//这里是提取关键的原始字符串！！！不用分割？为什么wcb哪里把他分割了啊....还有我应该【0】元素是没有我要的东西的，从1开始？
                for (int i = 1; i < rawSplit.length; i++) {
//                    String basicInfo = Utils.betweenStrings(rawSplit[i], "<div class=\"item clearfix\"", "<div class=\"details\" >");
//                    String details = Utils.betweenStrings(rawSplit[i], "<div class=\"details\" >", "<div class=\"alignPanel\">");
                    ContentInfo cmi;
                    cmi = new ContentInfo();
                    coursemessage_list.add(cmi);
                }

                adapter.updateList(coursemessage_list);
                // 显示课程列表的fancy的动画
                mRecyclerView.scheduleLayoutAnimation();
            }
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
     * 为了方便管理课程信息列表，将每个课程的各种信息组成一个类。
     */
    public class ContentInfo {
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
