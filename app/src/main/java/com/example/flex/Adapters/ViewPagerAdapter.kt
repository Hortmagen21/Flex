package com.example.flex.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private val fragmentList= mutableListOf<Fragment>()
    private val titlesList= mutableListOf<String>()
    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titlesList[position]
    }
    fun addFragment(fragment: Fragment,title:String){
        fragmentList.add(fragment)
        titlesList.add(title)
    }
}