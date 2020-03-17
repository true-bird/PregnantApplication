package com.example.jhw.newexapplication;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class BlockPagerAdapter extends FragmentStatePagerAdapter {
    // BlockSeatInfo 리스트를 받아와 BlockFragment에 연결
    static ArrayList<BlockSeatInfo> mArrayList;

    public BlockPagerAdapter(FragmentManager fm, ArrayList<BlockSeatInfo> mArrayList) {
        super(fm);
        this.mArrayList=mArrayList;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public Fragment getItem(int i) {
        return BlockFragment.newInstance(i); // 매개변수 전달
    }

    @Override
    public int getCount() {
        return 10;
    }
}
