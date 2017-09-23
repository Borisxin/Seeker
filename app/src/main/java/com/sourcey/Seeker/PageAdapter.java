package com.sourcey.Seeker;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by gn963 on 2017/3/23.
 */

public class PageAdapter extends PagerAdapter {

    private List<View> mViewList;

    public PageAdapter(List<View> mViewList){
        this.mViewList=mViewList;
    }
    @Override
    public int getCount() {
        if(mViewList!=null) {
            return mViewList.size();
        }
        else{
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Item" + (position + 1);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position){
        container.addView(mViewList.get(position));
        return (mViewList.get(position));
    }
    @Override
    public void destroyItem(ViewGroup container,int position,Object object){
        container.removeView((View) object);
    }
}