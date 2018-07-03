package com.shiftpsh.sensortester.main

import android.databinding.ObservableInt
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewPager
import com.shiftpsh.sensortester.BaseViewModel
import com.shiftpsh.sensortester.R
import com.shiftpsh.sensortester.extensions.onPropertyChanged
import timber.log.Timber

class MainViewModel : BaseViewModel() {

    val currentPage = ObservableInt(0)
    val currentMenuItem = ObservableInt(R.id.item_camera_rear)

    override fun onCreate() {
        currentPage.onPropertyChanged { sender, propertyId ->
            currentMenuItem.set(when (currentPage.get()) {
                0 -> R.id.item_camera_rear
                1 -> R.id.item_camera_front
                2 -> R.id.item_sensors
                else -> 0
            })
        }
    }

    override fun onResume() {
    }

    override fun onPause() {
    }

    override fun onDestroy() {
    }

    val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
        currentPage.set(it.order)
        Timber.d("NavigationItemSelected: %d", it.order)
        true
    }

    val onPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            currentPage.set(position)
            Timber.d("PageScrolled: %d", position)
        }

        override fun onPageSelected(position: Int) {
            currentPage.set(position)
            Timber.d("PageSelected: %d", position)
        }
    }
}