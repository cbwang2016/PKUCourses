package edu_cn.pku.course.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import edu_cn.pku.course.Utils;
import edu_cn.pku.course.activities.R;

public class SpareClassroomsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SpareClassroomsLoadingTask mLoadingTask = null;
    private SwipeRefreshLayout mSpareClassroomsSwipeContainer;
    private WebView mWebView;

    public SpareClassroomsFragment() {
        // Required empty public constructor
    }

    public static SpareClassroomsFragment newInstance() {
        return new SpareClassroomsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_spare_classrooms, container, false);
        // 查找xml文件中的对象并保存进Java变量
        mSpareClassroomsSwipeContainer = linearLayout.findViewById(R.id.spare_classrooms_swipe_container);
        mWebView = linearLayout.findViewById(R.id.spare_classrooms_web_view);

        mSpareClassroomsSwipeContainer.setOnRefreshListener(this);

        // 显示Loading的小动画，并在后台读取课程列表
        showLoading(true);
        mLoadingTask = new SpareClassroomsLoadingTask();
        mLoadingTask.execute((Void) null);

        return linearLayout;
    }

    @Override
    public void onRefresh() {
        if (mLoadingTask == null) {
            mLoadingTask = new SpareClassroomsLoadingTask();
            mLoadingTask.execute((Void) null);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showLoading(final boolean show) {
        // 逐渐显示mRecyclerView的小动画
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mWebView.setVisibility(show ? View.GONE : View.VISIBLE);
        mWebView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mWebView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mSpareClassroomsSwipeContainer.setRefreshing(show);
    }

    @SuppressLint("StaticFieldLeak")
    private class SpareClassroomsLoadingTask extends AsyncTask<Void, Void, String> {

        SpareClassroomsLoadingTask() {
        }

        @Override
        protected String doInBackground(Void... params) {
            return Utils.getPortalSession();
        }

        @SuppressLint("SetJavaScriptEnabled")
        @Override
        protected void onPostExecute(final String session) {

            // 出现了错误
            if (session.startsWith(Utils.errorPrefix)) {
                Snackbar.make(mSpareClassroomsSwipeContainer, session, Snackbar.LENGTH_SHORT).show();
            } else {
                CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(mWebView.getContext());
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.setAcceptCookie(true);
                cookieManager.removeSessionCookie();
                cookieManager.setCookie("https://portal.w.pku.edu.cn", "_astraeus_session=" + session + "; Domain=.pku.edu.cn");
                cookieSyncManager.sync();

                mWebView.getSettings().setJavaScriptEnabled(true);
                mWebView.setWebViewClient(new WebViewClient() {
                    boolean loadingFinished = true;
                    boolean redirect = false;

                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest r) {
                        if (!loadingFinished) {
                            redirect = true;
                        }

                        loadingFinished = false;
                        mWebView.loadUrl(r.getUrl().toString());
                        return true;
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        loadingFinished = false;
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        if (!redirect) {
                            loadingFinished = true;
                        }

                        if (loadingFinished && !redirect) {
                            view.loadUrl("javascript:document.getElementById('fav_freeclassroom').click();");
                            mLoadingTask = null;
                            showLoading(false);
                        } else {
                            redirect = false;
                        }
                    }
                });
                mWebView.loadUrl("https://portal.w.pku.edu.cn/portal2017/#/pub/freeClassroom");
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
