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
import android.widget.LinearLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

import edu_cn.pku.course.Utils;
import edu_cn.pku.course.activities.R;
import edu_cn.pku.course.adapter.AnnouncementBodyAdapter;

public class AnnouncementBodyFragment extends Fragment {

    private static final String AnnouncementId = "param1";
    private AnnouncementBodyLoadingTask mLoadingTask = null;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mAnnouncementBodySwipeContainer;
    private AnnouncementBodyAdapter adapter;
    private String announcementId;

    public AnnouncementBodyFragment() {
        // Required empty public constructor
    }

    public static AnnouncementBodyFragment newInstance(String announcementId) {
        AnnouncementBodyFragment fragment = new AnnouncementBodyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        args.putString(AnnouncementId, announcementId);
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
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_announcement_body, container, false);
        // 查找xml文件中的对象并保存进Java变量
        mRecyclerView = linearLayout.findViewById(R.id.recycler_announcement_body);
        mAnnouncementBodySwipeContainer = linearLayout.findViewById(R.id.announcement_body_swipe_container);

        // 设置动画
//        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);
//       mAnnouncementBodySwipeContainer.setLayoutAnimation(animation);
        // 设置刷新的监听类为此类（监听函数onRefresh）
        mAnnouncementBodySwipeContainer.setEnabled(false);

        FragmentActivity fa = getActivity();
        if (fa == null) {
            announcementId = "";
            return linearLayout;
        }
        adapter = new AnnouncementBodyAdapter(new ArrayList<AnnouncementListFragment.AnnouncementInfo>(), this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);
        announcementId = getActivity().getIntent().getStringExtra("AnnouncementId");

        // 显示Loading的小动画，并在后台读取课程列表
        showLoading(true);
        mLoadingTask = new AnnouncementBodyLoadingTask();
        mLoadingTask.execute((Void) null);

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

        mAnnouncementBodySwipeContainer.setRefreshing(show);
    }

    @SuppressLint("StaticFieldLeak")
    private class AnnouncementBodyLoadingTask extends AsyncTask<Void, Void, String> {

        AnnouncementBodyLoadingTask() {
        }

        @Override
        protected String doInBackground(Void... params) {
            return Utils.courseHttpGetRequest("http://course.pku.edu.cn/webapps/blackboard/execute/announcement?method=search&returnUrl=/webapps/portal/execute/tabs/tabAction?tab_tab_group_id=_3_1&tabId=_1_1");
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
                Element n = Jsoup.parse(str).getElementById(announcementId);
                ArrayList<AnnouncementListFragment.AnnouncementInfo> announcement_body_list = new ArrayList<>();
                announcement_body_list.add(new AnnouncementListFragment.AnnouncementInfo(n));
                adapter.updateList(announcement_body_list);
            }
        }

        @Override
        protected void onCancelled() {
            mLoadingTask = null;
            showLoading(false);
        }
    }

}
