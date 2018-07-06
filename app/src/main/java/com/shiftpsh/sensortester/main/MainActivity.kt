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
import com.shiftpsh.sensortester.sensorinfo.SensorInfoFragment
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel
    var lastPage = 0

    private val cameraInfoFragments = ArrayList<CameraInfoFragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = MainViewModel()
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            vm = viewModel
        }

        val initialIndex = savedInstanceState?.getInt("index") ?: 0
        lastPage = initialIndex

        cameraInfoFragments.removeAll(cameraInfoFragments)

        setSupportActionBar(ui_toolbar)
        requestPermission(Manifest.permission.CAMERA) { initialize(initialIndex) }
        lifecycle.addObserver(viewModel)
    }

    fun initialize(initialIndex: Int) {
        Timber.d("MainActivity init")
        val tempAdapter = ViewPagerAdapter(supportFragmentManager)

        CameraInfoFragment().let { fragment ->
            val bundleRear = Bundle()
            bundleRear.putString("facing", Facing.REAR.name)
            fragment.arguments = bundleRear
            tempAdapter += fragment
            cameraInfoFragments += fragment

            onFocusChanged(fragment, 0)
        }

        CameraInfoFragment().let { fragment ->
            val bundleFront = Bundle()
            bundleFront.putString("facing", Facing.FRONT.name)
            fragment.arguments = bundleFront
            tempAdapter += fragment
            cameraInfoFragments += fragment

            onFocusChanged(fragment, 1)
        }

        SensorInfoFragment().let {
            tempAdapter += it
        }

        ui_fragment_container.offscreenPageLimit = 3
        ui_fragment_container.adapter = tempAdapter

        // https://stackoverflow.com/questions/19316729/android-viewpager-setcurrentitem-not-working-after-onresume
        ui_fragment_container.post {
            ui_fragment_container.setCurrentItem(initialIndex, false)
            for (i in cameraInfoFragments.indices) {
                onFocusChanged(cameraInfoFragments[i], i)
            }
            viewModel.onCurrentPageChanged(lastPage)
        }

        viewModel.currentPageFlowable.subscribe {
            lastPage = it

            for (i in cameraInfoFragments.indices) {
                onFocusChanged(cameraInfoFragments[i], i)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putInt("index", lastPage)
        super.onSaveInstanceState(outState)
    }

    fun onFocusChanged(fragment: CameraInfoFragment, idx: Int) {
        fragment.onFocusChanged(lastPage == idx)
    }
}
