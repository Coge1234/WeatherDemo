package com.example.viewpagertest.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2017/2/10.
 */

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragmentList;
    private FragmentManager fm;
    private boolean[] flags;//标识,重新设置fragment时全设为true

    public ScreenSlidePagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fm = fm;
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return null == fragmentList || fragmentList.isEmpty() ? 0 : fragmentList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (null != flags && flags[position]) {
            /**得到缓存的fragment, 拿到tag并替换成新的fragment*/
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            String fragmentTag = fragment.getTag();
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(fragment);
            fragment = fragmentList.get(position);
            ft.add(container.getId(), fragment, fragmentTag);
            ft.attach(fragment);
            ft.commit();
            /**替换完成后设为false*/
            flags[position] = false;
            if (null != fragment) {
                return fragment;
            } else {
                return super.instantiateItem(container, position);
            }
        } else {
            return super.instantiateItem(container, position);
        }

    }

    public void setFragments(List<Fragment> fragmentList) {
        if (this.fragmentList != null) {
            flags = new boolean[fragmentList.size()];
            for (int i = 0; i < fragmentList.size(); i++) {
                flags[i] = true;
            }
        }
        this.fragmentList = fragmentList;
        notifyDataSetChanged();
    }
}
