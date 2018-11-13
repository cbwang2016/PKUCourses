package edu_cn.pku.course.pkucourse;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu_cn.pku.course.adapter.FragmentAdapter;
import edu_cn.pku.course.fragments.AnnouncementsFragment;
import edu_cn.pku.course.fragments.CourseListFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ViewPager view_pager_main;
    NavigationView navigationView;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View parentView = navigationView.getHeaderView(0);
        TextView nameView = parentView.findViewById(R.id.nav_header_title);
        TextView schoolView = parentView.findViewById(R.id.nav_header_subtitle);
        SharedPreferences sharedPreferences = getSharedPreferences("login_info", Context.MODE_PRIVATE);
        nameView.setText(sharedPreferences.getString("name", null));
        schoolView.setText(sharedPreferences.getString("school", null));

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(CourseListFragment.newInstance());
        fragments.add(AnnouncementsFragment.newInstance("", ""));

        FragmentAdapter mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
        view_pager_main = findViewById(R.id.view_pager_main);
        view_pager_main.setAdapter(mFragmentAdapter);
        view_pager_main.addOnPageChangeListener(pageChangeListener);

        setTitle(navigationView.getMenu().getItem(0).getTitle());
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
//            if (colorListColors[position].equals("White")) {
//                img_main_toggle.setImageTintList(ColorStateList.valueOf(Color.WHITE));
//            } else {
//                img_main_toggle.setImageTintList(ColorStateList.valueOf(Color.BLACK));
//            }
            setTitle(navigationView.getMenu().getItem(position).getTitle());

            navigationView.getMenu().getItem(position).setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

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

        switch (id) {
            case R.id.nav_courselist:
                // Handle the camera action

                view_pager_main.setCurrentItem(0);
                break;
            case R.id.nav_announcements:

                view_pager_main.setCurrentItem(1);
                break;
            case R.id.nav_signout:
                new AlertDialog.Builder(this)
                        .setMessage("Do you really want to sign out?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                signOut();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                break;
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
}
