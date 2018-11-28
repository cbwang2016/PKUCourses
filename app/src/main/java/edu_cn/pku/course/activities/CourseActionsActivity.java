package edu_cn.pku.course.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import edu_cn.pku.course.adapter.CourseActionsAdapter;

public class CourseActionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_actions);
        RecyclerView course_actions_recycle = findViewById(R.id.recycler_actions);
        course_actions_recycle.setLayoutManager(new LinearLayoutManager(CourseActionsActivity.this));
        course_actions_recycle.setAdapter(new CourseActionsAdapter(CourseActionsActivity.this));
    }
}
