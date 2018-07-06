package com.shiftpsh.sensortester.main

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewPager
import com.shiftpsh.sensortester.BaseViewModel
import com.shiftpsh.sensortester.R
import com.shiftpsh.sensortester.camerainfo.Facing
import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor
import timber.log.Timber

class MainViewModel : BaseViewModel() {

    private val currentPageProcessor: PublishProcessor<Int> = PublishProcessor.create()
    internal val currentPageFlowable: Flowable<Int> = currentPageProcessor

    val currentPage = ObservableInt(0)
    val isCurrentTypeCamera = ObservableBoolean(true)
    val currentMenuItem = ObservableInt(R.id.item_camera_rear)
    val cameraAvailable = ObservableBoolean(false)
    val cameraFacing = ObservableField<Facing>(Facing.FRONT)

    private val cameraAvailableProcessor: PublishProcessor<Boolean> = PublishProcessor.create()
    internal val cameraAvailableFlowable: Flowable<Boolean> = cameraAvailableProcessor

    private val stateProcessor: PublishProcessor<Pair<Boolean, Facing>> = PublishProcessor.create()
    internal val stateFlowable: Flowable<Pair<Boolean, Facing>> = stateProcessor

    private var lastPage = -1

    val menu = arrayOf(
            R.id.item_camera_rear,
            R.id.item_camera_front,
            R.id.item_sensors
    )

    override fun onCreate() {
    }

    override fun onResume() {
    }

    override fun onPause() {
    }

    override fun onDestroy() {
    }

    fun onCameraAvailiabilityChanged(available: Boolean) {
        cameraAvailable.set(available)
        cameraAvailableProcessor.onNext(available)
    }

    fun onCurrentPageChanged(page: Int) {
        Timber.d("onCurrentPageChanged: $page")
        if (page != lastPage) {
            currentPageProcessor.onNext(page)

            currentPage.set(page)
            currentMenuItem.set(menu[page])

            lastPage = page

            when (page) {
                0 -> {
                    isCurrentTypeCamera.set(true)
                    cameraFacing.set(Facing.REAR)

                    stateProcessor.onNext(true to Facing.REAR)
                }
                1 -> {
                    isCurrentTypeCamera.set(true)
                    cameraFacing.set(Facing.FRONT)

                    stateProcessor.onNext(true to Facing.FRONT)
                }
                2 -> {
                    isCurrentTypeCamera.set(false)

                    stateProcessor.onNext(false to Facing.REAR)
                }
            }
        }
    }

    val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val index = menu.indexOf(item.itemId)
        onCurrentPageChanged(index)
        true
    }
}

enum class Page {
    CAMERA_REAR, CAMERA_FRONT, SENSOR
}