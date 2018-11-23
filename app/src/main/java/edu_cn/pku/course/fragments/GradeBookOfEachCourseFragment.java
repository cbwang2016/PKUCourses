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
import edu_cn.pku.course.adapter.GradeBookListRecyclerViewAdapter;


public class GradeBookOfEachCourseFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private LoadingTask mLoadingTask = null;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mGradeBookSwipeContainer;
    private GradeBookListRecyclerViewAdapter adapter;
    private static String CourseId = null;

    public GradeBookOfEachCourseFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     *
     * @return A new instance of fragment CoursesListFragment.
     */
    public static GradeBookOfEachCourseFragment newInstance(String courseId) {
        GradeBookOfEachCourseFragment fragment = new GradeBookOfEachCourseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        CourseId = courseId;
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
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_grade_book_list, container, false);
        // 查找xml文件中的对象并保存进Java变量
        mRecyclerView = linearLayout.findViewById(R.id.recycler_grades);
        mGradeBookSwipeContainer = linearLayout.findViewById(R.id.grade_book_list_swipe_container);

        // 设置动画
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);
        mGradeBookSwipeContainer.setLayoutAnimation(animation);
        // 设置刷新的监听类为此类（监听函数onRefresh）
        mGradeBookSwipeContainer.setOnRefreshListener(this);

        FragmentActivity fa = getActivity();
        // 为了消除编译器Warning，需要判断一下是不是null，其实这基本上不可能出现null
        if (fa == null) {
            Snackbar.make(mRecyclerView, "null getActivity!", Snackbar.LENGTH_SHORT).show();
            return linearLayout;
        }
        adapter = new GradeBookListRecyclerViewAdapter(new ArrayList<GradeInfo>());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);

        // 显示Loading的小动画，并在后台读取课程列表
        showLoading(true);
        mLoadingTask = new LoadingTask();
        mLoadingTask.execute((Void) null);

        return linearLayout;
    }


    @Override
    public void onRefresh() {
        if (mLoadingTask == null) {
            mLoadingTask = new LoadingTask();
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

        mGradeBookSwipeContainer.setRefreshing(show);
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadingTask extends AsyncTask<Void, Void, String> {

        LoadingTask() {
        }

        //主要是有个course id一串数字是不一样的
        @Override
        protected String doInBackground(Void... params) {
            return Utils.courseHttpGetRequest("http://course.pku.edu.cn/webapps/gradebook/do/student/viewGrades?course_id=" + CourseId);
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
                String[] rawSplit = str.split("return mygrades_utils.showComment");
                ArrayList<GradeInfo> grade_list = new ArrayList<>();

                FragmentActivity fa = getActivity();
                if (fa == null) {
                    Snackbar.make(mRecyclerView, "null getActivity!", Snackbar.LENGTH_SHORT).show();
                    return;
                }
//这里是提取关键的原始字符串！！
                for (int i = 1; i < rawSplit.length; i++) {
                    String rawGrade = Utils.betweenStrings(rawSplit[i], "<td headers=\"grade", "/span>");//希望可以啊...这个函数万一有空的？
                    String title = Utils.betweenStrings(rawSplit[i], "'TH')\">", "</a>");
                    String description = Utils.betweenStrings(rawSplit[i], "\"vtbegenerated\"> ", "</div>");
                    GradeInfo ai;
                    ai = new GradeInfo(rawGrade, title, description);
                    grade_list.add(ai);
                }

                adapter.updateList(grade_list);
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


    /**
     * 为了方便管理成绩，将每个成绩的各种信息组成一个类。
     */
    public static class GradeInfo {
        private String rawGrade;
        private String title;
        private String description;


        GradeInfo(String str1, String str2, String str3) {
            rawGrade = str1; //格式： 2">
            // <a href="/webapps/blackboard/execute/showGroupSubmissionHistory?course_id=_42720_1&outcome_definition_id=_126690_1&outcome_id=_1960565_1"><strong>10.00</strong></a><span class="out-of">/10
            title = str2;
            description = str3;
        }

        public String getGrade() {
            return Utils.betweenStrings(rawGrade, "><strong>", "</strong></a>");
        }

        public String getDetailedGrade() {
            return Utils.betweenStrings(rawGrade, "><strong>", "</strong></a>") + Utils.betweenStrings(rawGrade, "\"out-of\">", "<");
        }

        public String getGradeTitle() {
            return title;
        }

        public String getGradeDescription() {
            return description;
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

