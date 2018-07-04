package com.shiftpsh.sensortester.camerainfo.item

import android.databinding.ObservableField
import android.view.View
import com.shiftpsh.sensortester.BaseItemViewModel

class CameraPropertyViewModel : BaseItemViewModel<CameraProperty>() {

    val property = ObservableField<CameraProperty>(
            CameraProperty("", 0, "")
    )

    override fun setItem(item: CameraProperty) {
        property.set(item)
    }

    fun onClick(view: View) {
        property.get()?.click(view.context)
    }

}