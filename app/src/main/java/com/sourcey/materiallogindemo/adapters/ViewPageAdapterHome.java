package com.sourcey.materiallogindemo.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.sourcey.materiallogindemo.ui.FindFragment;
import com.sourcey.materiallogindemo.ui.FriendsFragment;
import com.sourcey.materiallogindemo.ui.PetsFragment;
import com.sourcey.materiallogindemo.ui.ProfileFragment;

import java.util.HashMap;
import java.util.Map;

public class ViewPageAdapterHome extends FragmentStatePagerAdapter {

    CharSequence Titles[];
    int NumbOfTabs;
    private Map<Integer, Fragment> mPageReferenceMap = new HashMap<Integer, Fragment>();

    public ViewPageAdapterHome(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
    }

    @Override
    public Fragment getItem(int position) {

        switch(position){
            case 0:{
                ProfileFragment profileFragment = new ProfileFragment();
                mPageReferenceMap.put(0, profileFragment);
                return profileFragment;
            }
            case 1:{
                FriendsFragment friendsFragment = new FriendsFragment();
                mPageReferenceMap.put(1, friendsFragment);
                return friendsFragment;

            }
            case 2: {
                FindFragment findFragment = new FindFragment();
                mPageReferenceMap.put(2, findFragment);
                return findFragment;
            }
            case 3:{
                PetsFragment petsFragment = new PetsFragment();
                mPageReferenceMap.put(3, petsFragment);
                return petsFragment;

            }
            default:break;
        }

        return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        mPageReferenceMap.remove(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    @Override
    public int getCount() {
        return NumbOfTabs;
    }

}