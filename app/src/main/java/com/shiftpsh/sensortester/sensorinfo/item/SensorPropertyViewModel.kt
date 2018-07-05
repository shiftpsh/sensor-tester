package com.shiftpsh.sensortester.sensorinfo.item

import android.databinding.ObservableField
import android.view.View
import com.shiftpsh.sensortester.BaseItemViewModel

class SensorPropertyViewModel : BaseItemViewModel<SensorProperty>() {

    val property = ObservableField<SensorProperty>(
            SensorProperty(SensorType.NULL, "-", false)
    )

    override fun setItem(item: SensorProperty) {
        property.set(item)
    }

    fun onClick(view: View) {
        property.get()?.click(view.context)
    }

}