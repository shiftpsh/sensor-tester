package com.shiftpsh.sensortester.main

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.shiftpsh.sensortester.R
import com.shiftpsh.sensortester.camerainfo.CameraInfoFragment
import com.shiftpsh.sensortester.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setSupportActionBar(ui_toolbar)

        val tempAdapter = ViewPagerAdapter(supportFragmentManager)
        repeat(3) {
            tempAdapter += CameraInfoFragment()
        }

        ui_fragment_container.adapter = tempAdapter
    }
}
