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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

import edu_cn.pku.course.Utils;
import edu_cn.pku.course.activities.LoginActivity;
import edu_cn.pku.course.activities.R;
import edu_cn.pku.course.adapter.GradeBookListRecyclerViewAdapter;

import static edu_cn.pku.course.Utils.errorPrefix;

public class GradeBookOfEachCourseFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static String CourseId = "Param1";
    private LoadingTask mLoadingTask = null;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mGradeBookSwipeContainer;
    private GradeBookListRecyclerViewAdapter adapter;
    private String courseId;

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
        args.putString(CourseId, courseId);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseId = getArguments().getString(CourseId);
        }
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
        mGradeBookSwipeContainer.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorAccent));

        FragmentActivity fa = getActivity();
        // 为了消除编译器Warning，需要判断一下是不是null，其实这基本上不可能出现null
        if (fa == null) {
            return linearLayout;
        }
        adapter = new GradeBookListRecyclerViewAdapter(new ArrayList<GradeInfo>());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);
        courseId = getActivity().getIntent().getStringExtra("CourseId");

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
        int shortAnimTime = 200;

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

    private void attachXmlToView(Node nNode) {
        ArrayList<GradeInfo> grade_list = new ArrayList<>();
        if (nNode != null) {
            NodeList nList = nNode.getChildNodes();
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node n = nList.item(temp);
                GradeInfo gi = new GradeInfo(Utils.nodeToString(n));
                grade_list.add(gi);
            }
        }
        adapter.updateList(grade_list);
        // 显示课程列表的fancy的动画
        mRecyclerView.scheduleLayoutAnimation();
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

    /**
     * 为了方便管理成绩，将每个成绩的各种信息组成一个类。
     */
    public static class GradeInfo {
        private Element eElement;

        GradeInfo(String str) {
            Node temp = Utils.stringToNode(str);
            eElement = (Element) temp;
        }

        public String getGrade() {
            return eElement.getAttribute("grade");
        }

        public String getDetailedGrade() {
            if (eElement.getAttribute("pointspossible").equals("0.0"))
                return eElement.getAttribute("grade") + "/" + "-";
            return eElement.getAttribute("grade") + "/" + eElement.getAttribute("pointspossible");
        }

        public String getGradeTitle() {
            return eElement.getAttribute("name");
        }

        public String getGradeDescription() {
            if (eElement.getAttribute("comments") == null)
                return "";
            return eElement.getAttribute("comments");
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadingTask extends AsyncTask<Void, Void, String> {

        LoadingTask() {
        }

        //主要是有个course id一串数字是不一样的
        @Override
        protected String doInBackground(Void... params) {
            return Utils.courseHttpGetRequest("http://course.pku.edu.cn/webapps/Bb-mobile-bb_bb60/courseData?course_section=GRADES&course_id=" + courseId);
        }

        @Override
        protected void onPostExecute(final String str) {
            mLoadingTask = null;
            showLoading(false);

            // 出现了错误
            if (str.startsWith(errorPrefix)) {
                if (str.equals(errorPrefix + Utils.errorPasswordIncorrect)) {
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
                ArrayList<GradeInfo> grade_list = new ArrayList<>();

                FragmentActivity fa = getActivity();
                if (fa == null) {
                    return;
                }

                Node rootNode = Utils.stringToNode(str);
                if (rootNode != null) {
                    attachXmlToView(rootNode.getFirstChild());
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
}

