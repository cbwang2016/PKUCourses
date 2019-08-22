package edu_cn.pku.course.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;
import java.util.ArrayList;

import li.filedirchoose.ChooseFileActivity;

public class SelectFolderActivity extends Activity{
    public static final int PATHREQUESTCODE = 44;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        ChooseFileActivity.enterActivityForResult(this, PATHREQUESTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("zww","onActivityResult ");
        if (requestCode == PATHREQUESTCODE && resultCode == ChooseFileActivity.RESULTCODE) {
            ArrayList<String> resPath = data.getStringArrayListExtra(ChooseFileActivity.SELECTPATH);
            Log.e("ZWW", resPath.toString());
            if(resPath.size() > 0) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("path_preference", resPath.get(0).toString());
                editor.apply();
            }
        }
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }
}
