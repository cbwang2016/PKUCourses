package edu_cn.pku.course.activities;

import android.content.Intent;
import android.os.Bundle;
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
        getMenuList();

        ListView listView = (ListView) findViewById(R.id.about_listView);

        final AboutAdapter adapter = new AboutAdapter(AboutActivity.this, mList);
        listView.setAdapter(adapter);
        //ListView item的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        Intent j = new Intent(Intent.ACTION_SEND);
                        // j.setType("text/plain"); //模拟器请使用这行
                        j.setType("message/rfc822"); // 真机上使用这行
                        j.putExtra(Intent.EXTRA_EMAIL,
                                new String[] { "wcb@pku.edu.cn" });
                        j.putExtra(Intent.EXTRA_SUBJECT, "您发现的问题及建议");
                        j.putExtra(Intent.EXTRA_TEXT, "我们很希望能得到您的建议！！！");
                        startActivity(Intent.createChooser(j,
                                "Select email application."));
                      break;
                    case 1:
                        Toast.makeText(AboutActivity.this, "Click item" + 9, Toast.LENGTH_SHORT).show();
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
        mList.add(new AboutMenu(R.mipmap.icon_email_600,"问题反馈（请用email打开）"));
        mList.add(new AboutMenu(R.mipmap.icon_member_980,"开发人员"));
        mList.add(new AboutMenu(R.mipmap.icon_function_768,"功能介绍"));
        mList.add(new AboutMenu(R.mipmap.icon_github_logo_1024,"Github Page"));
    }

    public class AboutMenu{
        private int imageId;
        private String menu;

        AboutMenu(int imageId, String menu){
            this.imageId = imageId;
            this.menu = menu;
        }

        public String getMenu(){
            return menu;
        }
        public void setMenu(){
        }
        public int getImageId(){
            return imageId;
        }
        public void setImageId(){
        }
    }
}



