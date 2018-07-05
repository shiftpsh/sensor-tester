package com.shiftpsh.sensortester.sensorinfo.item

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.hardware.SensorManager
import android.os.Build
import android.widget.ArrayAdapter

class SensorProperty(val type: SensorType, val value: String, val supported: Boolean) {
    fun click(context: Context) {
        if (type == SensorType.NULL) return
        if (!supported) return
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(type.id)

        val details = ArrayList<String>()
        details += "Name = " + sensor.name
        details += "Vendor = " + sensor.vendor
        details += "Version = " + sensor.version
        details += "Delay = %.2f .. %.2f ms".format(sensor.minDelay / 1000.0f, sensor.maxDelay / 1000.0f)
        details += if (sensor.resolution < 0.01f) {
            "Resolution = %.4e ".format(sensor.resolution) + type.unit
        } else {
            "Resolution = %.4f ".format(sensor.resolution) + type.unit
        }
        details += "Max Range = %.2f ".format(sensor.maximumRange) + type.unit
        details += "Power Consuming = %.2f mA".format(sensor.power)

        val builder = AlertDialog.Builder(context)
        builder.setAdapter(ArrayAdapter(context, android.R.layout.simple_list_item_1, details), DialogInterface.OnClickListener { dialogInterface, i ->

        })

        builder.setTitle(type.representation)
        builder.setIcon(type.icon)
        builder.setCancelable(true)

        builder.create().show()
    }
}

fun getSensorProperties(): ArrayList<SensorProperty> {
    val apiVersion = Build.VERSION.SDK_INT
    val temp = ArrayList<SensorProperty>()

    for (type in SensorType.values()) {
        if (type == SensorType.NULL) continue

        if (type.apiLevel <= apiVersion) {
            temp += SensorProperty(
                    type, "-", false
            )
        } else {
            temp += SensorProperty(
                    type, "API ${type.apiLevel} needed", false
            )
        }
    }

    return temp
}