package edu_cn.pku.course.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.view.View;
//import android.widget.ArrayAdapter;
import android.widget.ListView;

//import java.util.ArrayList;
//import java.util.List;

import java.util.ArrayList;
import java.util.List;

import edu_cn.pku.course.adapter.AboutAdapter;

@SuppressLint("Registered")
public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        AboutAdapter aboutAdapter = new AboutAdapter(this, R.layout.item_about_list_view, About.getAbout());

        ListView listView = findViewById(R.id.about_listView);

        listView.setAdapter(aboutAdapter);

    }

    public static class About {

        private String menu;
        // 构造函数
        private About(String menu){
            this.menu = menu;
        }
        // 返回一个About列表
        public static List<About> getAbout(){
            List<About> abouts = new ArrayList<>();
            abouts.add(new About("问题反馈"));
            abouts.add(new About("开发人员"));
            abouts.add(new About("功能介绍"));
            abouts.add(new About("Github Page"));

            return abouts;
        }

        public String getMenu(){
            return menu;
        }
        public void setMenu(String menu){
            this.menu = menu;
        }


    }



}



