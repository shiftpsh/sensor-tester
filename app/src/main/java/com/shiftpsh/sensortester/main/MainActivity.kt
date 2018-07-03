package com.shiftpsh.sensortester.main

import android.Manifest
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.shiftpsh.sensortester.R
import com.shiftpsh.sensortester.camerainfo.CameraInfoFragment
import com.shiftpsh.sensortester.camerainfo.Facing
import com.shiftpsh.sensortester.databinding.ActivityMainBinding
import com.shiftpsh.sensortester.extension.requestPermission
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = MainViewModel()
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            vm = viewModel
        }

        setSupportActionBar(ui_toolbar)
        requestPermission(Manifest.permission.CAMERA) { initialize() }
        lifecycle.addObserver(viewModel)
    }

    fun initialize() {
        val tempAdapter = ViewPagerAdapter(supportFragmentManager)

        CameraInfoFragment().let {
            val bundleRear = Bundle()
            bundleRear.putString("facing", Facing.REAR.name)
            it.arguments = bundleRear
            tempAdapter += it
        }

        CameraInfoFragment().let {
            val bundleFront = Bundle()
            bundleFront.putString("facing", Facing.FRONT.name)
            it.arguments = bundleFront
            tempAdapter += it
        }

        // TODO replace this with SensorAdapter
        CameraInfoFragment().let {
            val bundleRear = Bundle()
            bundleRear.putString("facing", Facing.REAR.name)
            it.arguments = bundleRear
            tempAdapter += it
        }

        ui_fragment_container.adapter = tempAdapter
    }
}
