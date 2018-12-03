package edu_cn.pku.course.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import edu_cn.pku.course.fragments.AnnouncementListFragment;
import edu_cn.pku.course.fragments.CourseListFragment;
import edu_cn.pku.course.fragments.DashboardFragment;
import edu_cn.pku.course.fragments.MyGradeFragment;
import edu_cn.pku.course.fragments.SpareClassroomsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView navigationView;
    CourseListFragment courseListFragment;
    MyGradeFragment myGradeFragment;
    private ViewPager view_pager_main;
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            // 设置屏幕左上角的标题、以及左侧抽屉栏中被选中的对象
            setTitle(navigationView.getMenu().getItem(position).getTitle());
            navigationView.getMenu().getItem(position).setChecked(true);

            if (checkLongPressHint()) {
                if (position == 1)
                    courseListFragment.showLongPressHint();
                else if (position == 3)
                    myGradeFragment.showLongPressHint();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 模板自动生成
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // 查找xml文件中的对象并保存进Java变量
        navigationView = findViewById(R.id.nav_view);
        // 设置navigationView的事件监听类为这个类，监听函数为onNavigationItemSelected
        navigationView.setNavigationItemSelectedListener(this);

        // 将姓名和学校显示到navigationView的Header中的两个位置
        View parentView = navigationView.getHeaderView(0);
        TextView nameView = parentView.findViewById(R.id.nav_header_title);
        TextView schoolView = parentView.findViewById(R.id.nav_header_subtitle);
        SharedPreferences sharedPreferences = getSharedPreferences("login_info", Context.MODE_PRIVATE);
        nameView.setText(sharedPreferences.getString("name", null));
        schoolView.setText(sharedPreferences.getString("school", null));

        // 将CourseListFragment和AnnouncementListFragment两个fragment加入到view_pager_main内
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(DashboardFragment.newInstance());
        fragments.add(courseListFragment = CourseListFragment.newInstance());
        fragments.add(AnnouncementListFragment.newInstance());
        fragments.add(myGradeFragment = MyGradeFragment.newInstance());
        fragments.add(SpareClassroomsFragment.newInstance());
        FragmentAdapter mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
        view_pager_main = findViewById(R.id.view_pager_main);
        view_pager_main.setAdapter(mFragmentAdapter);
        view_pager_main.addOnPageChangeListener(pageChangeListener);

        // 设置屏幕左上角的标题、以及左侧抽屉栏中被选中的对象
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String indexStr = sp.getString("launch_page", "0");
        int launch_page_index = indexStr == null ? 0 : Integer.parseInt(indexStr);
        setTitle(navigationView.getMenu().getItem(launch_page_index).getTitle());
        navigationView.getMenu().getItem(launch_page_index).setChecked(true);
        view_pager_main.setCurrentItem(launch_page_index);
    }

    private boolean checkLongPressHint() {
        SharedPreferences sharedPreferences = getSharedPreferences("longPressHint", Context.MODE_PRIVATE);
        return !sharedPreferences.getBoolean("showed", false);
    }

    @Override
    public void onBackPressed() {
        // 模板自动生成的，大概是说如果左侧抽屉栏被打开，按返回键的时候关闭抽屉栏而不是退出程序
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
            case R.id.nav_dashboard:
                view_pager_main.setCurrentItem(0);
                break;
            case R.id.nav_courselist:
                view_pager_main.setCurrentItem(1);
                break;
            case R.id.nav_announcements:
                view_pager_main.setCurrentItem(2);
                break;
            case R.id.nav_mygrade:
                view_pager_main.setCurrentItem(3);
                break;
            case R.id.nav_spare_classrooms:
                view_pager_main.setCurrentItem(4);
                break;
            case R.id.nav_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_about:
                Intent intentAbout = new Intent(this, AboutActivity.class);
                startActivity(intentAbout);
                break;
            case R.id.nav_signout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
                builder.setMessage("你确定要注销吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        signOut();
                    }
                });
                builder.setNegativeButton("取消", null).show();
                break;
        }

        // 关闭抽屉栏
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        // 清除存储的登陆信息
        SharedPreferences sharedPreferences = getSharedPreferences("login_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // 回到LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
