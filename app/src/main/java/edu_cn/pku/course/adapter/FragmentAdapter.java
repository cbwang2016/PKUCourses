package edu_cn.pku.course.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * 别人写的程序，其实用处不大。
 * 这个好像是用来换页需要的？
 * Created by zhang on 2016.08.07.
 */
public class FragmentAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragments;

    public FragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

}
