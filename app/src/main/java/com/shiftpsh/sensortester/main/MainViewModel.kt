package com.shiftpsh.sensortester.main

import android.databinding.BindingAdapter
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.support.design.widget.BottomNavigationView
import android.view.Menu
import android.view.View
import com.shiftpsh.sensortester.BaseViewModel
import com.shiftpsh.sensortester.R
import com.shiftpsh.sensortester.camerainfo.Facing
import com.shiftpsh.sensortester.extension.Camera2Extensions
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
    val waitingCamera = ObservableBoolean(true)
    val aspectRatio = ObservableField<String>("9:16")
    val cameraFacing = ObservableField<Facing>(Facing.FRONT)

    private val cameraAvailableProcessor: PublishProcessor<Boolean> = PublishProcessor.create()
    internal val cameraAvailableFlowable: Flowable<Boolean> = cameraAvailableProcessor

    private val stateProcessor: PublishProcessor<Pair<Boolean, Facing>> = PublishProcessor.create()
    internal val stateFlowable: Flowable<Pair<Boolean, Facing>> = stateProcessor

    private val captureProcessor: PublishProcessor<Boolean> = PublishProcessor.create()
    internal val captureFlowable: Flowable<Boolean> = captureProcessor

    private var lastPage = -1

    val menu = Camera2Extensions.availableCameraIdFacings()

    override fun onCreate() {
    }

    override fun onResume() {
    }

    override fun onPause() {
    }

    override fun onDestroy() {
    }

    fun onCameraWaitingStateChanged(waiting: Boolean) {
        waitingCamera.set(waiting)
    }

    fun onCameraAvailiabilityChanged(available: Boolean) {
        cameraAvailable.set(available)
        cameraAvailableProcessor.onNext(available)
    }

    fun onCurrentPageChanged(page: Int, force: Boolean = false) {
        if (lastPage != page || force) {
            Timber.d("onCurrentPageChanged: $page")
            currentPageProcessor.onNext(page)

            currentPage.set(page)
            currentMenuItem.set(menu[page].first.hashCode())

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
            }
        }
    }

    fun onCapture(v: View) {
        captureProcessor.onNext(true)
    }

    val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        for (m in menu) {
            if (m.first.hashCode() == item.itemId) {
                onCurrentPageChanged(menu.indexOf(m))
                break
            }
        }
        true
    }
}

@BindingAdapter("app:bindMenu")
fun bindMenu(view: BottomNavigationView, vm: MainViewModel) {
    val menus = vm.menu

    for (menu in menus) {
        view.menu.add(Menu.NONE, menu.first.hashCode(), Menu.NONE, "${menu.second} - ID ${menu.first}").setIcon(menu.second.icon)
    }
}