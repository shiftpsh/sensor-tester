package com.shiftpsh.sensortester.main

import android.databinding.ObservableInt
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewPager
import com.shiftpsh.sensortester.BaseViewModel
import com.shiftpsh.sensortester.R
import com.shiftpsh.sensortester.extension.onPropertyChanged

class MainViewModel : BaseViewModel() {

    val currentPage = ObservableInt(0)
    val currentMenuItem = ObservableInt(R.id.item_camera_rear)

    val menu = arrayOf(
            R.id.item_camera_rear,
            R.id.item_camera_front,
            R.id.item_sensors
    )

    override fun onCreate() {
        currentPage.onPropertyChanged { sender, propertyId ->
            currentMenuItem.set(menu[currentPage.get()])
        }
    }

    override fun onResume() {
    }

    override fun onPause() {
    }

    override fun onDestroy() {
    }

    val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val index = menu.indexOf(item.itemId)
        currentPage.set(index)
        true
    }

    val onPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
            currentPage.set(position)
        }
    }
}