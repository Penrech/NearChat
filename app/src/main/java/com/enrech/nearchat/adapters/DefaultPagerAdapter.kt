package com.enrech.nearchat.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

//Este es el adapter por defecto usado para cambiar de un fragment a otro en los diferentes viewPager
class DefaultPagerAdapter(fragmentManager: FragmentManager, private var listFragment: List<Fragment>): FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        return listFragment[position]
    }

    override fun getCount(): Int {
        return listFragment.size
    }
}