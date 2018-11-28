package edu_cn.pku.course;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import edu_cn.pku.course.activities.R;
import edu_cn.pku.course.activities.SplashActivity;

public class Utils {
    public static final String errorPrefix = "Error: ";
    public static final String errorPasswordIncorrect = "Password Incorrect";
    public static final String errorSubstrings = "error when extracting substrings";

    private static final Context applicationContext = SplashActivity.getContextOfApplication();

    /**
     * 将字符串str中第一个出现的leftStr和第一个出现的rightStr之间的字符串提取出来
     * 感觉有一个问题，l是不是应该小于leftStr.length()?
     */
    public static String betweenStrings(String str, String leftStr, String rightStr) {
        int l = str.indexOf(leftStr) + leftStr.length();
        int r = str.indexOf(rightStr);
        if (l < leftStr.length() || r < 0 || l > r)
            return errorPrefix + errorSubstrings;
        return str.substring(l, r);
    }

    /**
     * 将字符串str中最后出现的leftStr和最后出现的rightStr之间的字符串提取出来
     */
    public static String lastBetweenStrings(String str, String leftStr, String rightStr) {
        int l = str.lastIndexOf(leftStr) + leftStr.length();
        int r = str.lastIndexOf(rightStr);
        if (l < leftStr.length() || r < 0 || l > r)
            return errorPrefix + errorSubstrings;
        return str.substring(l, r);
    }

    private static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static String courseHttpGetRequest(String requestURL) {
        return courseHttpRequest("GET", requestURL, null);
    }

    public static String courseHttpPostRequest(String requestURL, String urlParameters) {
        return courseHttpRequest("POST", requestURL, urlParameters);
    }

    private static String courseHttpRequest(String method, String requestURL, String urlParameters) {
        if (!isNetworkAvailable()) {
            return errorPrefix + applicationContext.getString(R.string.network_error);
        }

        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences("login_info", Context.MODE_PRIVATE);
        String session_id = sharedPreferences.getString("session_id", null);

        if (session_id == null) {
            return errorPrefix + "null session_id";
        }

        HttpURLConnection conn = null;
        try {
            URL url = new URL(requestURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("Cookie", "session_id=" + session_id);
            conn.setUseCaches(false);
            conn.setRequestMethod(method);
            if (method.equals("POST")) {
                byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postData);
            }
            if (conn.getResponseCode() == 302) {
                // session expired
                String results = renewSession();
                if (results.equals("")) {
                    session_id = sharedPreferences.getString("session_id", null);

                    url = new URL(requestURL);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setInstanceFollowRedirects(false);
                    conn.setRequestProperty("Cookie", "session_id=" + session_id);
                    conn.setUseCaches(false);
                    conn.setRequestMethod(method);
                    if (method.equals("POST")) {
                        byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
                        conn.setDoOutput(true);
                        conn.getOutputStream().write(postData);
                    }

                    InputStream in = conn.getInputStream();

                    return convertStreamToString(in);
                } else {
                    return errorPrefix + results;
                }
            }
            InputStream in = conn.getInputStream();

            return convertStreamToString(in);
        } catch (Exception e) {
            return errorPrefix + e.getMessage();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static String renewSession() {
        HttpURLConnection conn = null;

        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences("login_info", Context.MODE_PRIVATE);
        String mStudentId = sharedPreferences.getString("student_id", null);
        String mPassword = sharedPreferences.getString("password", null);
        try {
            String urlParameters = "appid=blackboard&userName=" + URLEncoder.encode(mStudentId, "UTF-8") + "&password=" + URLEncoder.encode(mPassword, "UTF-8") + "&randCode=&smsCode=&otpCode=&redirUrl=http%3A%2F%2Fcourse.pku.edu.cn%2Fwebapps%2Fbb-sso-bb_bb60%2Fexecute%2FauthValidate%2FcampusLogin";
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

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("session_id", session_id);
                editor.apply();

                return "";
            } else if (str.contains("\"success\":false")) {
                return errorPasswordIncorrect;
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
}
