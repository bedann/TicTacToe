package com.intersofteagles.tictactoe.Commoners;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Monroe on 7/13/2016.
 */
public class PagerAdapter extends FragmentPagerAdapter {

    List<Fragment> fragments = new ArrayList<>();
    List<String> titles = new ArrayList<>();
    PageListener pageListener;
    Context context;



    public PagerAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context = context;
//        pageListener = (PageListener)context;
    }


    public void addTitle(String title){
        titles.add(title);
    }

    public void addFragment(Fragment fragment){
        fragments.add(fragment);
    }


    public void addFrag(Fragment fragment,String title){
        fragments.add(fragment);
        titles.add(title.toUpperCase());
    }

    public void addAllFrags(Fragment... frag){
        fragments.addAll(Arrays.asList(frag));
    }

    public void addAllTitles(String... title){
        titles.addAll(Arrays.asList(title));
    }

    public void removeFrag(int i){
        fragments.remove(i);
        titles.remove(i);
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        pageListener.pageCount(fragments.size());
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }


    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public interface PageListener{
        void pageCount(int size);
    }

}
