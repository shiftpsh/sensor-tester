package com.shiftpsh.sensortester.main

import android.Manifest
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.ViewUtils
import com.shiftpsh.sensortester.R
import com.shiftpsh.sensortester.camerainfo.CameraInfoFragment
import com.shiftpsh.sensortester.camerainfo.Facing
import com.shiftpsh.sensortester.databinding.ActivityMainBinding
import com.shiftpsh.sensortester.extension.onPropertyChanged
import com.shiftpsh.sensortester.extension.requestPermission
import com.shiftpsh.sensortester.sensorinfo.SensorInfoFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = MainViewModel()
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            vm = viewModel
        }

        val initialIndex = savedInstanceState?.getInt("index") ?: 0

        setSupportActionBar(ui_toolbar)
        requestPermission(Manifest.permission.CAMERA) { initialize(initialIndex) }
        lifecycle.addObserver(viewModel)
    }

    fun initialize(initialIndex: Int) {
        val tempAdapter = ViewPagerAdapter(supportFragmentManager)

        CameraInfoFragment().let {
            val bundleRear = Bundle()
            bundleRear.putString("facing", Facing.REAR.name)
            it.arguments = bundleRear
            tempAdapter += it
            it.focused.set(viewModel.currentPage.get() == 0)

            viewModel.currentPage.onPropertyChanged { sender, propertyId ->
                it.focused.set(viewModel.currentPage.get() == 0)
            }
        }

        CameraInfoFragment().let {
            val bundleFront = Bundle()
            bundleFront.putString("facing", Facing.FRONT.name)
            it.arguments = bundleFront
            tempAdapter += it
            it.focused.set(viewModel.currentPage.get() == 1)

            viewModel.currentPage.onPropertyChanged { sender, propertyId ->
                it.focused.set(viewModel.currentPage.get() == 1)
            }
        }

        SensorInfoFragment().let {
            tempAdapter += it
        }

        ui_fragment_container.offscreenPageLimit = 3
        ui_fragment_container.adapter = tempAdapter

        // https://stackoverflow.com/questions/19316729/android-viewpager-setcurrentitem-not-working-after-onresume
        ui_fragment_container.post {
            ui_fragment_container.setCurrentItem(initialIndex, false)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putInt("index", viewModel.currentPage.get())
        super.onSaveInstanceState(outState)
    }
}
