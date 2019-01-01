package edu_cn.pku.course.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

import edu_cn.pku.course.Utils;
import edu_cn.pku.course.activities.R;
import edu_cn.pku.course.adapter.CourseActionsAdapter;

public class CourseActionFragment extends Fragment {
    private String courseId;

    private CourseActionFragment.ActionsLoadingTask mLoadingTask = null;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mCourseActionSwipeContainer;
    private CourseActionsAdapter adapter;

    public CourseActionFragment() {
        // Required empty public constructor
    }

    /**
     * 传递参数用的，这里没用到。
     *
     * @return A new instance of fragment CourseListFragment.
     */
    public static CourseActionFragment newInstance() {
        CourseActionFragment fragment = new CourseActionFragment();
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
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_course_action, container, false);
        // 查找xml文件中的对象并保存进Java变量
        mRecyclerView = linearLayout.findViewById(R.id.recycler_actions);
        mCourseActionSwipeContainer = linearLayout.findViewById(R.id.course_action_swipe_container);

        // 设置动画
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);
        mCourseActionSwipeContainer.setLayoutAnimation(animation);
        // 设置刷新的监听类为此类（监听函数onRefresh）
        mCourseActionSwipeContainer.setEnabled(false);

        adapter = new CourseActionsAdapter(new ArrayList<String>(), this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);

        FragmentActivity fa = getActivity();
        if (fa == null) {
            courseId = "";
        } else {
            String xmlStr = getActivity().getIntent().getStringExtra("CourseActionsXML");
            if (xmlStr == null) {
                courseId = getActivity().getIntent().getStringExtra("CourseId");
                // 显示Loading的小动画，并在后台读取课程列表
                showLoading(true);
                mLoadingTask = new CourseActionFragment.ActionsLoadingTask();
                mLoadingTask.execute((Void) null);
            } else {
                attachXmlToView(Utils.stringToNode(xmlStr));
            }
        }

        return linearLayout;
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

        mCourseActionSwipeContainer.setRefreshing(show);
    }

    private void attachXmlToView(Node nNode) {
        ArrayList<String> actions_list = new ArrayList<>();
        if (nNode != null) {
            NodeList nList = nNode.getChildNodes();
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node n = nList.item(temp);
                actions_list.add(Utils.nodeToString(n));
            }
        }

        adapter.updateList(actions_list);
        // 显示课程列表的fancy的动画
        mRecyclerView.scheduleLayoutAnimation();
    }

    @SuppressLint("StaticFieldLeak")
    private class ActionsLoadingTask extends AsyncTask<Void, Void, String> {

        ActionsLoadingTask() {
        }

        @Override
        protected String doInBackground(Void... params) {
            return Utils.courseHttpGetRequest("http://course.pku.edu.cn/webapps/Bb-mobile-bb_bb60/courseMap?course_id=" + courseId);
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
                        Utils.SignOut(getActivity());
                    } catch (Exception e) {
                        if (mRecyclerView.isAttachedToWindow())
                            Snackbar.make(mRecyclerView, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    // 其他网络错误
                    if (mRecyclerView.isAttachedToWindow())
                        Snackbar.make(mRecyclerView, str, Snackbar.LENGTH_SHORT).show();
                }
            } else {

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
