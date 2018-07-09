package com.shiftpsh.sensortester.camerainfo.item

import android.databinding.ObservableField
import android.view.View
import com.shiftpsh.sensortester.BaseItemViewModel
import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor
import timber.log.Timber

class CameraPropertyViewModel : BaseItemViewModel<CameraProperty>() {

    private val cameraPropertiesProcessor: PublishProcessor<Pair<DefaultCameraProperty, String>> = PublishProcessor.create()
    internal val cameraPropertiesFlowable: Flowable<Pair<DefaultCameraProperty, String>> = cameraPropertiesProcessor

    val property = ObservableField<CameraProperty>(
            CameraProperty(DefaultCameraProperty.ZOOM, "")
    )

    override fun setItem(item: CameraProperty) {
        property.set(item)
    }

    fun onClick(view: View) {
        property.get()?.click(view.context, this)
    }

    fun onCameraPropertiesChange(newValue: String) {
        val dcp = property.get()?.property ?: return
        val newProperty = property.get()
        newProperty?.value = newValue
        newProperty?.modified = true
        property.set(newProperty)
        Timber.d("$dcp changed to ${property.get()?.value}")
        cameraPropertiesProcessor.onNext(dcp to newValue)
    }

}