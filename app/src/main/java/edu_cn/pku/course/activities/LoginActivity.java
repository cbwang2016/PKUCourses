package edu_cn.pku.course.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu_cn.pku.course.Utils;

/**
 * A login screen that offers login via studentid/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private TextView mStudentidView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupActionBar();


        // 查找xml文件中的对象并保存进Java变量
        mStudentidView = (EditText) findViewById(R.id.studentid);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mPasswordView = findViewById(R.id.password);

        // 模板自动生成的
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        final Button mqueryButton = findViewById(R.id.query_button);
        mqueryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.AlertDialogTheme);
                builder.setMessage("声明：\n所有用户的密码将不会被开发者获取，如仍有疑问，可访问\"https://github.com/cbwang2016/PKUCourses\"查看源码，谢谢您的信任。");
                builder.setPositiveButton("好的", null).create().show();
            }
        });

        // 给按钮studentid_sign_in_button增加事件监听函数，onClick事件发生时就执行attemptLogin。
        Button mstudentidSignInButton = findViewById(R.id.studentid_sign_in_button);
        mstudentidSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        setTitle("请登录校园网账号");
    }

    /**
     * Set up the {@link ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid studentid, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mStudentidView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String studentid = mStudentidView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid studentid address.
        if (TextUtils.isEmpty(studentid)) {
            mStudentidView.setError(getString(R.string.error_field_required));
            focusView = mStudentidView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(studentid, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = 200;

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    @SuppressLint("StaticFieldLeak")
    private class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mstudentid;
        private final String mPassword;

        UserLoginTask(String studentid, String password) {
            mstudentid = studentid;
            mPassword = password;
        }

        String convertStreamToString(java.io.InputStream is) {
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }

        private boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        @Override
        protected String doInBackground(Void... params) {

            if (!isNetworkAvailable()) {
                return getString(R.string.network_error);
            }

            HttpURLConnection conn = null;
            try {
                // 通过iaaa登陆
                String urlParameters = "appid=blackboard&userName=" + URLEncoder.encode(mstudentid, "UTF-8") + "&password=" + URLEncoder.encode(mPassword, "UTF-8") + "&randCode=&smsCode=&otpCode=&redirUrl=http%3A%2F%2Fcourse.pku.edu.cn%2Fwebapps%2Fbb-sso-bb_bb60%2Fexecute%2FauthValidate%2FcampusLogin";
                byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
                String request = "https://iaaa.pku.edu.cn/iaaa/oauthlogin.do";
                URL url = new URL(request);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setInstanceFollowRedirects(false);
                conn.setRequestMethod("POST");
                conn.setUseCaches(false);
                conn.getOutputStream().write(postData);
//                int status = conn.getResponseCode();
                InputStream in = conn.getInputStream();

                String str = convertStreamToString(in);

                conn.disconnect();

                if (str.contains("\"success\":true")) {
                    // 将iaaa返回的token提交给course来获取session_id
                    String token = Utils.betweenStrings(str, "\"token\":\"", "\"}");
                    request = "http://course.pku.edu.cn/webapps/bb-sso-bb_bb60/execute/authValidate/campusLogin?rand=0.5&token=" + token;
                    url = new URL(request);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setInstanceFollowRedirects(false);
                    Map<String, List<String>> headerFields = conn.getHeaderFields();
                    List<String> cookiesHeader = headerFields.get("Set-Cookie");

                    String session_id = null;
                    if (cookiesHeader != null)
                        for (String cookie : cookiesHeader)
                            if (cookie.contains("session_id="))
                                session_id = Utils.betweenStrings(cookie, "session_id=", "; Path=/;");

                    if (session_id == null)
                        throw new Exception("session_id not found");

                    conn.disconnect();

                    // get Basic info
                    request = "http://course.pku.edu.cn/webapps/portal/execute/topframe?tab_tab_group_id=_3_1&frameSize=LARGE";
                    url = new URL(request);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setInstanceFollowRedirects(false);
                    conn.setRequestProperty("Cookie", "session_id=" + session_id);
                    in = conn.getInputStream();
                    str = convertStreamToString(in);
                    String infos = Utils.betweenStrings(str, "<span id=\"loggedInUserName\">", "</span>");

                    // 存储进SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("login_info", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("session_id", session_id);
                    editor.putString("student_id", mstudentid);
                    editor.putString("password", mPassword);
                    editor.putString("name", infos.split(" ")[1]);
                    editor.putString("school", infos.split(" ")[0]);
                    editor.apply();

                    return "";
                } else if (str.contains("\"success\":false")) {
                    return "Password Incorrect";
                } else {
                    return "iaaa connect failed";
                }
            } catch (Exception e) {
//                e.printStackTrace();
                return e.getMessage();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(final String str) {
            mAuthTask = null;
            showProgress(false);

            switch (str) {
                case "":
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case "Password Incorrect":
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                    break;
                default:
                    View contextView = findViewById(R.id.context_view);
                    Snackbar.make(contextView, str, Snackbar.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

