package edu_cn.pku.course.pkucourse;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu_cn.pku.course.Utils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    /**
     * Keep track of the loading task to ensure we can cancel it if requested.
     */
    private CoursesLoadingTask mLoadingTask = null;
    private View mProgressView;
    private ListView mListView;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View parentView = navigationView.getHeaderView(0);
        TextView nameView = parentView.findViewById(R.id.nav_header_title);
        TextView schoolView = parentView.findViewById(R.id.nav_header_subtitle);
        SharedPreferences sharedPreferences = getSharedPreferences("login_info", Context.MODE_PRIVATE);
        nameView.setText(sharedPreferences.getString("name", null));
        schoolView.setText(sharedPreferences.getString("school", null));

        mListView = findViewById(R.id.coursesListView);
        mProgressView = findViewById(R.id.loading_progress);

        showProgress(true);
        mLoadingTask = new CoursesLoadingTask();
        mLoadingTask.execute((Void) null);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_courselist) {
            // Handle the camera action
        } else if (id == R.id.nav_signout) {
            new AlertDialog.Builder(this)
                    .setMessage("Do you really want to sign out?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            signOut();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        SharedPreferences sharedPreferences = getSharedPreferences("login_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
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
                        (MainActivity.this, android.R.layout.simple_list_item_1, courses_list);
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
}
