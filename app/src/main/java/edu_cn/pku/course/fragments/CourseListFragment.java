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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu_cn.pku.course.Utils;
import edu_cn.pku.course.adapter.CourseListRecyclerViewAdapter;
import edu_cn.pku.course.activities.LoginActivity;
import edu_cn.pku.course.activities.R;

public class CourseListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private CoursesLoadingTask mLoadingTask = null;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mCourseListSwipeContainer;

//    private OnFragmentInteractionListener mListener;

    public CourseListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
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
        mRecyclerView = linearLayout.findViewById(R.id.recycler_courses);

        mCourseListSwipeContainer = linearLayout.findViewById(R.id.course_list_swipe_container);
        mCourseListSwipeContainer.setOnRefreshListener(this);

        showLoading(true);
        mLoadingTask = new CoursesLoadingTask();
        mLoadingTask.execute((Void) null);

        return linearLayout;
    }

    @Override
    public void onRefresh() {
        mLoadingTask = null;
        mCourseListSwipeContainer.setRefreshing(false);
        mLoadingTask = new CoursesLoadingTask();
        mLoadingTask.execute((Void) null);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showLoading(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

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
            return Utils.courseHttpPostRequest("http://course.pku.edu.cn/webapps/portal/execute/tabs/tabAction", "action=refreshAjaxModule&modId=_4_1&tabId=_1_1&tab_tab_group_id=_3_1");
        }

        @Override
        protected void onPostExecute(final String str) {
            mLoadingTask = null;
            showLoading(false);

            if (str.startsWith(Utils.errorPrefix)) {
                if (str.equals(Utils.errorPrefix + Utils.errorPasswordIncorrect)) {
                    try {
                        signOut();
                    } catch (Exception e) {
                        Snackbar.make(mRecyclerView, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(mRecyclerView, str, Snackbar.LENGTH_SHORT).show();
                }
            } else {
                String[] rawSplit = str.split("</li>");
                ArrayList<CourseInfo> courses_list = new ArrayList<>();

                FragmentActivity fa = getActivity();
                if (fa == null) {
                    Snackbar.make(mRecyclerView, "null getActivity!", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences sharedPreferences = fa.getSharedPreferences("pinnedCourseList", Context.MODE_PRIVATE);
                Set<String> hset = sharedPreferences.getStringSet("key", null);
                if (hset == null)
                    hset = new HashSet<>();

                for (int i = 0; i < rawSplit.length - 1; i++) {
                    String tmp = Utils.betweenStrings(rawSplit[i], "target=\"_top\">", "</a>").split(": ")[1];
                    CourseInfo ci = new CourseInfo(tmp);
                    if (hset.contains(tmp))
                        ci.setPinned(1);
                    courses_list.add(ci);
                }

                String tmp = "课程1 (上)(28-29学年第7学期)";
                CourseInfo ci = new CourseInfo(tmp);
                if (hset.contains(tmp))
                    ci.setPinned(1);
                courses_list.add(ci);
                tmp = "课程2 (上)(28-29学年第6学期)";
                ci = new CourseInfo(tmp);
                if (hset.contains(tmp))
                    ci.setPinned(1);
                courses_list.add(ci);

                CourseListRecyclerViewAdapter adapter = new CourseListRecyclerViewAdapter(courses_list, sharedPreferences);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecyclerView.setAdapter(adapter);
            }
        }

        @Override
        protected void onCancelled() {
            mLoadingTask = null;
            showLoading(false);
        }
    }

    public class CourseInfo implements Comparable<CourseInfo> {
        private String rawStr;//格式： 004-00432108-0006156320-1: 数学物理方法 (上)(18-19学年第1学期)
        private int isPinned;

        CourseInfo(String str) {
            rawStr = str;
            isPinned = 0;
        }

        public void setPinned(int i) {
            isPinned = i;
        }

        public int isPinned() {
            return isPinned;
        }

        public String getRawStr() {
            return rawStr;
        }

        public String getCourseName() {
            return rawStr.split("\\([0-9]")[0];
        }

        public String getSemesterString() {
            return Utils.lastBetweenStrings(rawStr, "(", ")");
        }

        private int getSemesterYear() {
            return Integer.parseInt(getSemesterString().split("-")[0]);
        }

        private int getSemesterNumber() {
            return Integer.parseInt(Utils.betweenStrings(rawStr, "学年第", "学期"));
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
