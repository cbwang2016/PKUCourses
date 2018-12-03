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
import java.util.HashSet;
import java.util.Set;

import edu_cn.pku.course.Utils;
import edu_cn.pku.course.activities.LoginActivity;
import edu_cn.pku.course.activities.R;
import edu_cn.pku.course.activities.SplashActivity;
import edu_cn.pku.course.adapter.CourseListRecyclerViewAdapter;

public class CourseListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private CoursesLoadingTask mLoadingTask = null;
    private RecyclerView mRecyclerView = null;
    private boolean showLongPressHintFlag = false;
    private SwipeRefreshLayout mCourseListSwipeContainer;
    private CourseListRecyclerViewAdapter adapter;




    public CourseListFragment() {
        // Required empty public constructor
    }

    public void showLongPressHint() {
        if (mRecyclerView == null || !mRecyclerView.isAttachedToWindow()) {
            showLongPressHintFlag = true;
            return;
        }
        Snackbar.make(mRecyclerView, "温馨提示：长按课程可以置顶", Snackbar.LENGTH_SHORT)
                .setAction("我知道了", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences sharedPreferences = SplashActivity.contextOfApplication.getSharedPreferences("longPressHint", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("showed", true);
                        editor.apply();
                    }
                })
                .show();
        showLongPressHintFlag = false;
    }

    /**
     * 传递参数用的，这里没用到。
     *
     * @return A new instance of fragment CourseListFragment.
     */
    public static CourseListFragment newInstance() {
        CourseListFragment fragment = new CourseListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_course_list, container, false);
        // 查找xml文件中的对象并保存进Java变量
        mRecyclerView = linearLayout.findViewById(R.id.recycler_courses);
        mCourseListSwipeContainer = linearLayout.findViewById(R.id.course_list_swipe_container);


        // 设置动画
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);
        mCourseListSwipeContainer.setLayoutAnimation(animation);
        // 设置刷新的监听类为此类（监听函数onRefresh）
        mCourseListSwipeContainer.setOnRefreshListener(this);


        FragmentActivity fa = getActivity();
        // 为了消除编译器Warning，需要判断一下是不是null，其实这基本上不可能出现null
        if (fa == null) {
            return linearLayout;
        }
        // 将读取已置顶课程列表的SharedPreferences传递给CourseListRecyclerViewAdapter
        SharedPreferences sharedPreferences = fa.getSharedPreferences("pinnedCourseList", Context.MODE_PRIVATE);
        adapter = new CourseListRecyclerViewAdapter(new ArrayList<CourseInfo>(), sharedPreferences, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);




        // 显示Loading的小动画，并在后台读取课程列表
        showLoading(true);
        mLoadingTask = new CoursesLoadingTask();
        mLoadingTask.execute((Void) null);
        return linearLayout;
    }

    @Override
    public void onRefresh() {
        if (mLoadingTask == null) {
            mLoadingTask = new CoursesLoadingTask();
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

        mCourseListSwipeContainer.setRefreshing(show);
    }

    @SuppressLint("StaticFieldLeak")
    private class CoursesLoadingTask extends AsyncTask<Void, Void, String> {

        CoursesLoadingTask() {
        }

        @Override
        protected String doInBackground(Void... params) {
            return Utils.courseHttpGetRequest("http://course.pku.edu.cn/webapps/Bb-mobile-bb_bb60/dashboard?course_type=ALL&with_notifications=false");
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
                Node rootNode = Utils.stringToNode(str);
                if (rootNode != null) {

                    FragmentActivity fa = getActivity();
                    if (fa == null) {
                        return;
                    }
                    SharedPreferences sharedPreferences = fa.getSharedPreferences("pinnedCourseList", Context.MODE_PRIVATE);
                    Set<String> hset = sharedPreferences.getStringSet("key", null);
                    if (hset == null)
                        hset = new HashSet<>();

                    ArrayList<CourseInfo> courses_list = new ArrayList<>();
                    NodeList nCoursesList = rootNode.getFirstChild().getChildNodes();
                    for (int temp = 0; temp < nCoursesList.getLength(); temp++) {
                        CourseInfo ci = new CourseInfo((Element) nCoursesList.item(temp));
                        if (hset.contains(ci.getRawCourseName()))
                            ci.setPinned(1);
                        courses_list.add(ci);
                    }

                    adapter.updateList(courses_list);
                    // 显示课程列表的fancy的动画
                    mRecyclerView.scheduleLayoutAnimation();

                    if (showLongPressHintFlag)
                        showLongPressHint();
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

    /**
     * 为了方便管理课程列表，将每个课程的各种信息组成一个类。
     */
    public class CourseInfo implements Comparable<CourseInfo> {
        private Element nNode;
        private int isPinned;

        CourseInfo(Element nNode) {
            this.nNode = nNode;
            isPinned = 0;
        }

        public void setPinned(int i) {
            isPinned = i;
        }

        public int isPinned() {
            return isPinned;
        }

        public String getCourseId() {
            return nNode.getAttribute("bbid");
        }

        public String getRawCourseName() {
            return nNode.getAttribute("name");
        }

        public String getCourseName() {
            return nNode.getAttribute("name").split("\\([0-9]")[0];
        }

        public String getSemesterString() {
            return Utils.lastBetweenStrings(nNode.getAttribute("name"), "(", ")");
        }

        private int getSemesterYear() {
            return Integer.parseInt(getSemesterString().split("-")[0]);
        }

        private int getSemesterNumber() {
            return Integer.parseInt(Utils.betweenStrings(nNode.getAttribute("name"), "学年第", "学期"));
        }

        @Override
        public int compareTo(CourseInfo comp) {
            if (this.isPinned != comp.isPinned)
                return comp.isPinned - this.isPinned;
            if (this.getSemesterYear() != comp.getSemesterYear())
                return comp.getSemesterYear() - this.getSemesterYear();
            if (this.getSemesterNumber() != comp.getSemesterNumber())
                return comp.getSemesterNumber() - this.getSemesterNumber();
            return this.getCourseName().compareTo(comp.getCourseName());
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
