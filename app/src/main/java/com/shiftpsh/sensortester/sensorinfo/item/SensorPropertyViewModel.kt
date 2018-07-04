package com.shiftpsh.sensortester.sensorinfo.item

import android.databinding.ObservableField
import com.shiftpsh.sensortester.BaseItemViewModel

// TODO add sensor details
class SensorPropertyViewModel : BaseItemViewModel<SensorProperty>() {

    val property = ObservableField<SensorProperty>(
            SensorProperty(SensorType.NULL, "-")
    )

    override fun setItem(item: SensorProperty) {
        property.set(item)
    }

}