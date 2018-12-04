package edu_cn.pku.course.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.r0adkll.slidr.Slidr;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu_cn.pku.course.Utils;
import edu_cn.pku.course.adapter.ContentViewAdapter;
import pub.devrel.easypermissions.EasyPermissions;

public class ContentViewActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, EasyPermissions.PermissionCallbacks {

    private static final int WRITE_REQUEST_CODE = 300;

    private AttachedFilesListLoadingTask mLoadingTask = null;
    private SwipeRefreshLayout mSwipeContainer;
    private RecyclerView mRecyclerView;
    private ContentViewAdapter adapter;
    private LinearLayout content_view_linear_layout;
    private TextView content_view_title, content_view_time;
    private WebView content_view_content_detail;
    private String course_id, content_id;

    private String currentDownloadUrl = "";
    private String currentDownloadFileName = "";

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        new DownloadFile().execute();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    public void startDownload(AttachedFileItem item) {

        currentDownloadUrl = item.getUrl();
        currentDownloadFileName = item.getFileName();
        if (item.isDownloaded()) {
            openFile(Utils.downloadFolder + currentDownloadFileName);
            return;
        }
        //check if app has permission to write to the external storage.
        if (EasyPermissions.hasPermissions(ContentViewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //Get the URL entered
            new DownloadFile().execute();

        } else {
            //If permission is not present request for the same.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                EasyPermissions.requestPermissions(ContentViewActivity.this, "PKU Courses是开源软件，绝不会滥用权限。请授权存储权限以下载文件。", WRITE_REQUEST_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    private void openFile(String filePath) {
        final Uri data = FileProvider.getUriForFile(getApplicationContext(), "edu_cn.pku.course", new File(filePath));
        grantUriPermission(getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String mimeType = myMime.getMimeTypeFromExtension(fileExt(filePath));
        newIntent.setDataAndType(data, mimeType);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        newIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }
    }

    public Drawable getIcon(String fileName) {
        final Intent innt = new Intent(Intent.ACTION_VIEW);
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        String mimeType = myMime.getMimeTypeFromExtension(fileExt(fileName));
        innt.setType(mimeType);
        final List<ResolveInfo> matches = getPackageManager().queryIntentActivities(innt, 0);
        if (matches.size() == 0)
            return null;
        return matches.get(0).loadIcon(getPackageManager());
    }

    @NonNull
    private String fileExt(String url) {
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return ".";
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.contains("%")) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.contains("/")) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();
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

    /**
     * Async Task to download file from URL
     */
    @SuppressLint("StaticFieldLeak")
    private class DownloadFile extends AsyncTask<Void, String, String> {

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;
//        private boolean isDownloaded;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(ContentViewActivity.this, R.style.AlertDialogTheme);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(Void... params) {
            int count;
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("login_info", Context.MODE_PRIVATE);
                String session_id = sharedPreferences.getString("session_id", null);

                URL url = new URL(currentDownloadUrl);
                URLConnection connection = url.openConnection();
                connection.setRequestProperty("Cookie", "session_id=" + session_id);
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(connection.getInputStream(), 8192);

                fileName = currentDownloadFileName;

                //External directory path to save file
                folder = Utils.downloadFolder;

                //Create androiddeft folder if it does not exist
                File directory = new File(folder);

                if (!directory.exists()) {
                    if (!directory.mkdirs()) {
                        throw new Exception("Error creating folder");
                    }
                }

                // Output stream to write file
                OutputStream output = new FileOutputStream(folder + fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                return folder + fileName;

            } catch (Exception e) {
                return Utils.errorPrefix + e.getMessage();
            }
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
            this.progressDialog.dismiss();

            if (message.startsWith(Utils.errorPrefix)) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                return;
            }

            Toast.makeText(getApplicationContext(), "File downloaded at: " + message, Toast.LENGTH_LONG).show();
            // Display File path after downloading
//            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

            adapter.refreshList(); // 刷新已下载文件
            openFile(message);
        }
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
                                            .format(
                                                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz", Locale.US)
                                                            .parse(contentNode.getAttribute("datemodified"))
                                            )
                            );
                        } catch (ParseException e) {
                            content_view_time.setText(contentNode.getAttribute("datemodified"));
                        }

                        if (contentNode.getAttribute("contenthandler").equals("resource/x-bb-externallink")) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(contentNode.getAttribute("viewUrl")));
                            startActivity(browserIntent);
                            finish();
                        }

                        if (contentNode.getAttribute("contenthandler").equals("resource/x-bb-assignment")) {
                            Intent intent = new Intent(ContentViewActivity.this, WebViewActivity.class);
                            intent.putExtra("Title", contentNode.getAttribute("title"));
                            intent.putExtra("WebViewUrl", contentNode.getAttribute("viewUrl"));
                            startActivity(intent);
                            finish();
                        }

                        if ((contentNode).getElementsByTagName("body").getLength() > 0 && contentNode.getElementsByTagName("body").item(0).getFirstChild() != null) {
                            content_view_content_detail.getSettings().setDefaultTextEncodingName("UTF-8");
                            content_view_content_detail.loadData(
                                    ((CharacterData) contentNode.getElementsByTagName("body").item(0).getFirstChild()).getData()
                                    , "text/html; charset=utf-8", null);
                            content_view_content_detail.setWebViewClient(new WebViewClient() {
                                @Override
                                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                    return true;
                                }

                                @Override
                                public void onLoadResource(WebView view, String url) {
                                    if (url.startsWith("http://course.pku.edu.cn")) {
                                        Intent intent = new Intent(ContentViewActivity.this, WebViewActivity.class);
                                        intent.putExtra("Title", "正在打开链接...");
                                        intent.putExtra("WebViewUrl", url.replaceFirst("http://course.pku.edu.cn", ""));
                                        startActivity(intent);
                                    } else {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                        startActivity(browserIntent);
                                    }
                                }
                            });
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
            mSwipeContainer.setEnabled(false);
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
        boolean downloaded = false;

        AttachedFileItem(Element n) {
            this.n = n;
        }

        public boolean isDownloaded() {
            return downloaded;
        }

        public void setDownloaded(boolean b) {
            downloaded = b;
        }

        public String getFileName() {
            return n.getAttribute("name");
        }

        public String getFileSize() {
            return Utils.readableFileSize(Integer.parseInt(n.getAttribute("filesize")));
        }

        public int getFileSizeInteger() {
            return Integer.parseInt(n.getAttribute("filesize"));
        }

        private String getUrl() {
            return "http://course.pku.edu.cn" + n.getAttribute("uri");
        }
    }
}
