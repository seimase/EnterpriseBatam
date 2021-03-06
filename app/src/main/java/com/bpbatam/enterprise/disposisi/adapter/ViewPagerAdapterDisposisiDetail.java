package com.bpbatam.enterprise.disposisi.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.bpbatam.enterprise.disposisi.fragment.frag_disposisi_detail_distribusi;
import com.bpbatam.enterprise.disposisi.fragment.frag_disposisi_detail_dokumen;
import com.bpbatam.enterprise.disposisi.fragment.frag_disposisi_detail_tertanda;

/**
 * Created by hp1 on 21-01-2015.
 */
public class ViewPagerAdapterDisposisiDetail extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapterDisposisiDetail(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);
 
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
 
    }
 
    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
 
        if(position == 0) // if the position is 0 we are returning the First tab
        {
            frag_disposisi_detail_dokumen tab1 = new frag_disposisi_detail_dokumen();
            return tab1;
        }else{
            frag_disposisi_detail_distribusi tab3 = new frag_disposisi_detail_distribusi();
            return tab3;
        }
 
 
    }
 
    // This method return the titles for the Tabs in the Tab Strip
 
    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }
 
    // This method return the Number of tabs for the tabs Strip
 
    @Override
    public int getCount() {
        return NumbOfTabs;
    }
    
    @Override
    public int getItemPosition(Object object) {
         return POSITION_NONE;
    }
}