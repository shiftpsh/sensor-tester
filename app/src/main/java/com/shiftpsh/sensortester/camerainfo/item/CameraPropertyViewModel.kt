package com.shiftpsh.sensortester.camerainfo.item

import android.databinding.ObservableField
import com.shiftpsh.sensortester.BaseItemViewModel

class CameraPropertyViewModel : BaseItemViewModel<CameraProperty>() {

    var property = ObservableField<CameraProperty>(
            CameraProperty("", "", "", 0){}
    )

    override fun setItem(item: CameraProperty) {
        property.set(item)
    }

    fun onClick() {
        property.get()?.details
    }

}