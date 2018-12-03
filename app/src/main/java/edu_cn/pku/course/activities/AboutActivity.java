package edu_cn.pku.course.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu_cn.pku.course.adapter.AboutAdapter;


public class AboutActivity extends AppCompatActivity {
    private List<AboutMenu> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle("About");
        getMenuList();

        ListView listView = (ListView) findViewById(R.id.about_listView);

        final AboutAdapter adapter = new AboutAdapter(AboutActivity.this, mList);
        listView.setAdapter(adapter);
        //ListView item的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                        startActivity(new Intent(AboutActivity.this, GitWebViewActivity.class));
                        break;
                    case 2:
                        Toast.makeText(AboutActivity.this, "Click item" + 8, Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(AboutActivity.this, "Click item" + 7, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    /**
     * 初始化数据
     */
    private void getMenuList() {
        mList.add(new AboutMenu(R.mipmap.icon_email_600, "问题反馈", "Select email application to send an email to the developer.", ""));
        mList.add(new AboutMenu(R.mipmap.icon_github_logo_1024, "Github Page", "Open source code in github.", ""));
        mList.add(new AboutMenu(R.mipmap.icon_function_768, "功能介绍", "Features", "还没想好怎么写"));
        mList.add(new AboutMenu(R.mipmap.icon_member_980, "开发人员", "Developer", ""));
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



