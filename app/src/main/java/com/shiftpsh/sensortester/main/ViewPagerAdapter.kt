package com.shiftpsh.sensortester.main

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    private val fragments = ArrayList<Fragment>()

    operator fun plusAssign(fragment: Fragment) = add(fragment)

    fun add(fragment: Fragment) {
        fragments.add(fragment)
    }

    override fun getItem(index: Int) = fragments[index]

    override fun getCount() = fragments.size
}