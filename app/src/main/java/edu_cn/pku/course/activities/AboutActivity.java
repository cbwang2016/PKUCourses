package edu_cn.pku.course.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

import edu_cn.pku.course.Utils;
import edu_cn.pku.course.adapter.AboutAdapter;


public class AboutActivity extends AppCompatActivity {
    private List<AboutMenu> mList = new ArrayList<>();

    @SuppressLint({"Assert", "InflateParams"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle("About");
        TextView version = findViewById(R.id.about_version_number);
        String versionStr = "版本号：" + Utils.versionString;
        version.setText(versionStr);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        getMenuList();

        ListView listView = findViewById(R.id.about_listView);

        final AboutAdapter adapter = new AboutAdapter(AboutActivity.this, mList);
        listView.setAdapter(adapter);

        //ListView item的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("Assert")
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        // “问题反馈”：发送邮件
                        Intent j = new Intent(Intent.ACTION_SEND);
                        // j.setType("text/plain"); //模拟器请使用这行
                        j.setType("message/rfc822"); // 真机上使用这行
                        j.putExtra(Intent.EXTRA_EMAIL, new String[]{"wcb@pku.edu.cn"});
                        j.putExtra(Intent.EXTRA_SUBJECT, "您发现的问题及建议");
                        j.putExtra(Intent.EXTRA_TEXT, "我们很希望能得到您的建议！！！");
                        startActivity(Intent.createChooser(j, "Select email application."));
                        break;
                    case 1:
                        Uri uri = Uri.parse("https://github.com/cbwang2016/PKUCourses");
                        Intent it = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(it);
                        break;
                    case 2:
                        Toast.makeText(AboutActivity.this, "别点了，再点功能也不会多的！\n不如点击“问题反馈”给予我们建议。", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(AboutActivity.this, "Bloom！\n这里有四只狗\n有意向可以选购", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        Slidr.attach(this);
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

    /**
     * 初始化数据
     */
    private void getMenuList() {
        mList.add(new AboutMenu(R.mipmap.icon_email_600, "问题反馈", "Select email application to send an email to the developer.", null));
        mList.add(new AboutMenu(R.mipmap.icon_github_logo_1024, "Github Page", "Open source code on GitHub.", null));
        mList.add(new AboutMenu(R.mipmap.icon_function_768, "功能介绍", "Features", "此APP包含了course.pku.edu.cn的部分常用功能，例如公告、通知、课程列表及每个课程包含的内容等，另外添加了北京大学门户中空闲教室查询的功能，希望为寻找空闲教室的同学提供便利。\n"));
        mList.add(new AboutMenu(R.mipmap.icon_member_980, "开发人员", "Developer", "本App由四个疯人院成员开发制作：\n\n" +
                "·叶林楠\n    主要负责人\n        技术总监\n\n" +
                "·Yinian\n    点子来源\n        吉祥物\n\n" +
                "·杨老师歌迷\n    艺术总监\n\n" +
                "·丣覭\n    ≥DDL\n        挂件\n"));
    }

    public class AboutMenu {
        private int imageId;
        private String menu;
        private String subMenu;
        private String content;

        AboutMenu(int imageId, String menu, String subMenu, String content) {
            this.imageId = imageId;
            this.menu = menu;
            this.subMenu = subMenu;
            this.content = content;
        }

        public String getMenu() {
            return menu;
        }

        public String getSubMenu() {
            return subMenu;
        }

        public int getImageId() {
            return imageId;
        }

        public String getContent() {
            return content;
        }
    }
}



