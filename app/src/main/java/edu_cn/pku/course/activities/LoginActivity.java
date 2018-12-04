package edu_cn.pku.course.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
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
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

    private final String privacyPolicy =
            "<h1>隐私政策（简短版）</h1>\n" +
                    "<p>PKU Courses是开源软件，源码位于<a href=\"https://github.com/cbwang2016/PKUCourses\">https://github.com/cbwang2016/PKUCourses</a>，我们不会主动记录任何与您身份关联的信息，当然更不会记录密码。我们的软件中使用了<a href=\"https://mtj.baidu.com\">百度统计</a>作为第三方工具，百度可能会记录如IP地址等信息用于统计。</a></p>\n" +
                    "<h1>隐私政策（完整版）</h1>\n" +
                    "<p>生效日期：2018年12月4日</p>\n" +
                    "<p>PKU Courses移动应用程序团队（以下简称“我们”）维护和运行PKU Courses移动应用程序。</p>\n" +
                    "<p>当您使用我们的服务以及与该数据相关联的选项时，此页面会告知您有关个人数据的收集，使用和披露的政策。我们的PKU Courses隐私政策通过<a href=\"https://www.freeprivacypolicy.com/free-privacy-policy-generator.php\">Free\n" +
                    "        Privacy Policy Website</a>进行管理。</p>\n" +
                    "<p>我们使用您的数据来提供和改进服务。使用本服务即表示您同意按照本政策收集和使用信息。除非本隐私政策另有规定，否则本隐私政策中使用的术语与我们的条款和条件具有相同的含义。</p>\n" +
                    "<h2>信息收集和使用</h2>\n" +
                    "<p>我们为各种目的收集了几种不同类型的信息，以便为您提供和改进我们的服务。</p>\n" +
                    "<h3>收集的数据类型</h3>\n" +
                    "<h4>个人资料</h4>\n" +
                    "<p>我们不会收集任何可用于联系或识别您身份的个人身份信息（“个人数据”）。</p>\n" +
                    "<h4>使用数据</h4>\n" +
                    "<p>当您通过移动设备访问服务时，百度统计可能会自动收集某些信息，包括但不限于您使用的移动设备类型，移动设备唯一ID，移动设备的IP地址，您的移动操作系统，您使用的移动互联网浏览器类型，唯一设备标识符和其他诊断数据（“使用数据”）。</p>\n" +
                    "<h2>数据的使用</h2>\n" +
                    "<p>PKU Courses将收集的数据用于的各种目的：</p>\n" +
                    "<ul>\n" +
                    "    <li>提供和维护服务</li>\n" +
                    "    <li>通知您有关我们服务的更改</li>\n" +
                    "    <li>允许您在选择时参与我们服务的互动功能</li>\n" +
                    "    <li>提供客户服务和支持</li>\n" +
                    "    <li>提供分析或有价值的信息，以便我们改进服务</li>\n" +
                    "    <li>监控服务的使用情况</li>\n" +
                    "    <li>检测，预防和解决技术问题</li>\n" +
                    "</ul>\n" +
                    "<h2>数据传输</h2>\n" +
                    "<p>您的信息可能会转移到您所在州，省，国家/地区或其他政府管辖范围之外的计算机上，并且这些计算机的数据保护法可能与您所在司法辖区的数据保护法不同。</p>\n" +
                    "<p>如果您位于中国境外并选择向我们提供信息，请注意我们会将数据（包括个人数据）传输到中国并在中国处理。</p>\n" +
                    "<p>如果您同意本隐私政策，然后提交此类信息即表示您同意该传输。</p>\n" +
                    "<p>PKU Courses将采取合理必要的所有步骤，以确保您的数据得到安全处理并符合本隐私政策，并且不会向组织或国家/地区传输您的个人数据（除非有控制措施），包括您的数据和其他个人信息的安全性。</p>\n" +
                    "<h2>披露数据</h2>\n" +
                    "<h3>法律要求</h3>\n" +
                    "<p>PKU Courses相信此类行为是必要的：</p>\n" +
                    "<ul>\n" +
                    "    <li>遵守法律义务</li>\n" +
                    "    <li>保护和捍卫PKU Courses的权利或财产</li>\n" +
                    "    <li>防止或调查与服务相关的可能的不当行为</li>\n" +
                    "    <li>为了保护服务或公众用户的人身安全</li>\n" +
                    "    <li>防止法律责任</li>\n" +
                    "</ul>\n" +
                    "<h2>数据安全性</h2>\n" +
                    "<p>您的数据的安全性对我们很重要，但请记住，没有通过互联网传输的方法或电子存储方法是100％安全的。虽然我们努力使用商业上可接受的方式来保护您的个人数据，但我们无法保证其绝对的安全性。</p>\n" +
                    "<h2>服务提供商</h2>\n" +
                    "<p>我们可能使用第三方公司和个人来促进我们的服务（“服务提供商”），代表我们提供服务，执行服务相关服务或协助我们分析我们的服务使用方式。</p>\n" +
                    "<p>这些第三方只有代表我们执行这些任务才能访问您的个人数据，并且有义务不将其用于任何其他目的。</p>\n" +
                    "<h3>统计分析</h3>\n" +
                    "<p>我们可能会使用第三方服务提供商来监控和分析我们服务的使用。</p>\n" +
                    "<ul>\n" +
                    "    <li>\n" +
                    "        <P><strong>百度统计</strong></p>百度统计是百度提供的网站分析服务，可跟踪和报告网站流量。百度使用收集的数据来跟踪和监控我们服务的使用情况。此数据与其他百度服务共享。百度可能会使用收集的数据对其自己的广告网络的广告进行背景化和个性化。</p>\n" +
                    "        <p>有关百度隐私政策的更多信息，请访问百度隐私条款网页：<a href=\"https://www.baidu.com/duty/yinsiquan.html\">https://www.baidu.com/duty/yinsiquan.html\n" +
                    "            </a></p>  \n" +
                    "    </li>\n" +
                    "</ul>\n" +
                    "<h2>与其他网站的链接</h2>\n" +
                    "<p>我们的服务可能包含指向非我们运营的其他网站的链接。如果您点击第三方链接，您将被引导至该第三方的网站。我们强烈建议您查看您访问的每个站点的隐私政策。</p>\n" +
                    "<p>我们无法控制任何第三方网站或服务的内容，隐私政策或做法，也不承担任何责任。</p>\n" +
                    "<h2>儿童隐私</h2>\n" +
                    "<p>我们的服务不适用于18岁以下的任何人（“儿童”）。</p>\n" +
                    "<p>我们不会故意收集18岁以下任何人的个人身份信息。如果您是父母或监护人并且您知道您的孩子已向我们提供了个人数据，请与我们联系。如果我们发现我们在未经父母同意的情况下收集了儿童的个人数据，我们会采取措施从我们的服务器中删除该信息。</p>\n" +
                    "<h2>对本隐私政策的更改</h2>\n" +
                    "<p>我们可能会不时更新我们的隐私政策。我们将通过在此页面上发布新的隐私政策来通知您任何更改。</p>\n" +
                    "<p>我们会在更改生效之前通过电子邮件和/或关于我们服务的明显通知通知您，并更新本隐私政策顶部的“生效日期”。</p>\n" +
                    "<p>建议您定期查看本隐私政策以了解任何变更。本隐私政策的更改在此页面上发布时有效。</p>\n" +
                    "<h2>与我们联系</h2>\n" +
                    "<p>如果您对本隐私政策有任何疑问，请与我们联系：</p>\n" +
                    "<ul>\n" +
                    "    <li>email: wcb@pku.edu.cn </li>\n" +
                    "</ul>";

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
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.AlertDialogTheme);

                TextView msg = new TextView(v.getContext());
                msg.setText(Html.fromHtml(privacyPolicy));
                msg.setMovementMethod(LinkMovementMethod.getInstance());
                builder.setView(msg);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                lp.setMargins(50, 50, 50, 50);
                msg.setLayoutParams(lp);

                ScrollView ll = new ScrollView(v.getContext());
                ll.addView(msg);

                builder.setView(ll);
//                builder.setMessage("声明：\n所有用户的密码将不会被开发者获取，如仍有疑问，可访问\"https://github.com/cbwang2016/PKUCourses\"查看源码，谢谢您的信任。");
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

