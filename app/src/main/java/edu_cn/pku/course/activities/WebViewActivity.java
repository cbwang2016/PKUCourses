package edu_cn.pku.course.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import edu_cn.pku.course.Utils;
import pub.devrel.easypermissions.EasyPermissions;

public class WebViewActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeContainer;
    private WebView mWebView;

    private String currentDownloadUrl = "";
    private String currentDownloadFileName = "";
    private static final int WRITE_REQUEST_CODE = 300;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        mSwipeContainer = findViewById(R.id.web_view_swipe_container);
        mWebView = findViewById(R.id.web_view);

        SharedPreferences sharedPreferences = getSharedPreferences("login_info", Context.MODE_PRIVATE);
        String session_id = sharedPreferences.getString("session_id", null);

        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(mWebView.getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();
        cookieManager.setCookie("http://course.pku.edu.cn", "session_id=" + session_id + "; Domain=.pku.edu.cn");
        cookieSyncManager.sync();

        setTitle(getIntent().getStringExtra("Title"));
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
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
            public void onPageFinished(final WebView view, String url) {
                if (!redirect) {
                    loadingFinished = true;
                }

                if (loadingFinished && !redirect) {
                    mSwipeContainer.setRefreshing(false);
                } else {
                    redirect = false;
                }
            }
        });

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                currentDownloadFileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
                currentDownloadUrl = url;
                startDownload();
            }
        });

        mSwipeContainer.setRefreshing(true);
        mWebView.loadUrl("http://course.pku.edu.cn" + getIntent().getStringExtra("WebViewUrl"));

//        Slidr.attach(this);
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

    @Override
    public void onRefresh() {
        mSwipeContainer.setRefreshing(false);
    }

    public void startDownload() {

        //check if app has permission to write to the external storage.
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //Get the URL entered
            new DownloadFile().execute();

        } else {
            //If permission is not present request for the same.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                EasyPermissions.requestPermissions(this, "PKU Courses是开源软件，绝不会滥用权限。请授权存储权限以下载文件。", WRITE_REQUEST_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
            this.progressDialog = new ProgressDialog(WebViewActivity.this, R.style.AlertDialogTheme);
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
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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

            openFile(message);
            finish();
        }
    }
}
