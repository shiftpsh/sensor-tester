package com.shiftpsh.sensortester.extension

import android.app.Activity
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

fun Activity.requestPermission(permission: String, body: () -> Unit) {
    Dexter.withActivity(this)
            .withPermission(permission)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    body()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    finish()
                }

                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                }
            }).check()
}