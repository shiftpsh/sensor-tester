package com.shiftpsh.sensortester.sensorinfo.item

import android.os.Build

class SensorProperty(val type: SensorType, val value: String)

fun getSensorProperties(): ArrayList<SensorProperty> {
    val apiVersion = Build.VERSION.SDK_INT
    val temp = ArrayList<SensorProperty>()

    for (type in SensorType.values()) {
        if (type == SensorType.NULL) continue

        if (type.apiLevel <= apiVersion) {
            temp += SensorProperty(
                    type, "-"
            )
        } // TODO
    }

    return temp
}