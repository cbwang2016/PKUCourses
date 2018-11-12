package edu_cn.pku.course.pkucourse;

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
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import edu_cn.pku.course.Utils;

public class CourseListActivity extends MainActivity {

    private CoursesLoadingTask mLoadingTask = null;
    private View mProgressView;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        mListView = findViewById(R.id.coursesListView);
        mProgressView = findViewById(R.id.loading_progress);

        showProgress(true);
        mLoadingTask = new CoursesLoadingTask();
        mLoadingTask.execute((Void) null);
    }

//    @Override
//    public void setContentView(int layoutResID) {
//        View fullView = getLayoutInflater().inflate(layoutResID, null);
//        super.setContentView(fullView);
//    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mListView.setVisibility(show ? View.GONE : View.VISIBLE);
        mListView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mListView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            showProgress(false);

            if (str.startsWith(Utils.errorPrefix)) {
                if (str.equals(Utils.errorPrefix + Utils.errorPasswordIncorrect))
                    signOut();
                else {
                    View contextView = findViewById(R.id.coursesListView);
                    Snackbar.make(contextView, str, Snackbar.LENGTH_SHORT).show();
                }
            } else {
                String[] rawSplit = str.split("</li>");
                List<String> courses_list = new ArrayList<>();
                for (int i = 0; i < rawSplit.length - 1; i++) {
                    courses_list.add(Utils.betweenStrings(rawSplit[i], "target=\"_top\">", "</a>"));
                }
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                        (CourseListActivity.this, android.R.layout.simple_list_item_1, courses_list);
                mListView.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onCancelled() {
            mLoadingTask = null;
            showProgress(false);
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
