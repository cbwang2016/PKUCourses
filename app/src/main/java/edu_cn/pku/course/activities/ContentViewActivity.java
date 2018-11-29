package edu_cn.pku.course.activities;

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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.r0adkll.slidr.Slidr;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu_cn.pku.course.Utils;
import edu_cn.pku.course.adapter.ContentViewAdapter;
import pub.devrel.easypermissions.EasyPermissions;

public class ContentViewActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, EasyPermissions.PermissionCallbacks {

    private AttachedFilesListLoadingTask mLoadingTask = null;
    private SwipeRefreshLayout mSwipeContainer;
    private RecyclerView mRecyclerView;
    private ContentViewAdapter adapter;
    private LinearLayout content_view_linear_layout;
    private TextView content_view_title, content_view_time;
    private WebView content_view_content_detail;
    private String course_id, content_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_view);
        setTitle(getIntent().getStringExtra("Title"));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        course_id = getIntent().getStringExtra("CourseId");
        content_id = getIntent().getStringExtra("content_id");

        mRecyclerView = findViewById(R.id.recycler_content_view_list);
        mSwipeContainer = findViewById(R.id.content_view_swipe_container);
        content_view_linear_layout = findViewById(R.id.content_view_linear_layout);
        content_view_title = findViewById(R.id.content_view_title);
        content_view_time = findViewById(R.id.content_view_time);
        content_view_content_detail = findViewById(R.id.content_view_content_detail);

        adapter = new ContentViewAdapter(new ArrayList<AttachedFileItem>(), this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);

        mSwipeContainer.setOnRefreshListener(this);

        showLoading(true);
        mLoadingTask = new AttachedFilesListLoadingTask();
        mLoadingTask.execute((Void) null);

        Slidr.attach(this);
    }

    @Override
    public void onRefresh() {
        if (mLoadingTask == null) {
            mLoadingTask = new AttachedFilesListLoadingTask();
            mLoadingTask.execute((Void) null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return (super.onOptionsItemSelected(item));
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showLoading(final boolean show) {
        // 逐渐显示mRecyclerView的小动画
        int shortAnimTime = 200;

        content_view_linear_layout.setVisibility(show ? View.GONE : View.VISIBLE);
        content_view_linear_layout.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mSwipeContainer.setRefreshing(show);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        adapter.startDownload();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @SuppressLint("StaticFieldLeak")
    private class AttachedFilesListLoadingTask extends AsyncTask<Void, Void, String> {
        AttachedFilesListLoadingTask() {
        }

        @Override
        protected String doInBackground(Void... params) {
            return Utils.courseHttpGetRequest("http://course.pku.edu.cn/webapps/Bb-mobile-bb_bb60/contentDetail?course_id=" + course_id + "&content_id=" + content_id);
        }

        @Override
        protected void onPostExecute(final String str) {

            // 出现了错误
            if (str.startsWith(Utils.errorPrefix)) {
                if (str.equals(Utils.errorPrefix + Utils.errorPasswordIncorrect)) {
                    // 密码错误
                    signOut();
                } else {
                    // 其他网络错误
                    Snackbar.make(mRecyclerView, str, Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Node rootNode = Utils.stringToNode(str);
                if (rootNode != null) {
                    Element contentNode = (Element) rootNode.getFirstChild();
                    if (contentNode != null) {

                        content_view_title.setText((contentNode).getAttribute("title"));
                        try {
                            content_view_time.setText(
                                    new SimpleDateFormat("yyyy年M月d日 H:m:s", Locale.CHINA)
                                            .format(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz", Locale.US)
                                                    .parse(contentNode.getAttribute("datemodified"))
                                            )
                            );
                        } catch (ParseException e) {
                            content_view_time.setText(contentNode.getAttribute("datemodified"));
                        }

                        if ((contentNode).getElementsByTagName("body").getLength() > 0 && contentNode.getElementsByTagName("body").item(0).getFirstChild() != null) {
                            content_view_content_detail.getSettings().setDefaultTextEncodingName("UTF-8");
                            content_view_content_detail.loadData(
                                    ((CharacterData) contentNode.getElementsByTagName("body").item(0).getFirstChild()).getData()
                                    , "text/html; charset=utf-8", null);
                        } else {
                            content_view_content_detail.setVisibility(View.GONE);
                        }

                        if ((contentNode).getElementsByTagName("attachments").getLength() > 0) {
                            ArrayList<AttachedFileItem> item_list = new ArrayList<>();
                            NodeList nList = (contentNode).getElementsByTagName("attachments").item(0).getChildNodes();
                            for (int i = 0; i < nList.getLength(); i++) {
                                item_list.add(new AttachedFileItem((Element) nList.item(i)));
                            }

                            adapter.updateList(item_list);
                        }
                    }
                }
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

    public class AttachedFileItem {
        Element n;

        AttachedFileItem(Element n) {
            this.n = n;
        }

        public String getTitle() {
            return n.getAttribute("name");
        }

        public String getFileSize() {
            return Utils.readableFileSize(Integer.parseInt(n.getAttribute("filesize")));
        }

        public String getUrl() {
            return "http://course.pku.edu.cn" + n.getAttribute("uri");
        }
    }


    public void signOut() {
        SharedPreferences sharedPreferences = getSharedPreferences("login_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
