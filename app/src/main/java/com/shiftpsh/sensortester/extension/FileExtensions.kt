package com.shiftpsh.sensortester.extension

import android.os.Environment
import java.io.File


val PICTURES_DIRECTORY: File
    get() {
        val sdDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File(sdDir, "SensorTester")
    }