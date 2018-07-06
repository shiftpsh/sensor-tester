package com.shiftpsh.sensortester.camerainfo

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.hardware.Camera
import com.shiftpsh.sensortester.BaseViewModel
import com.shiftpsh.sensortester.R
import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor

class CameraInfoViewModel : BaseViewModel() {

    val cameraAvailable = ObservableBoolean(false)
    val cameraFacing = ObservableField<Facing>(Facing.FRONT)

    private val cameraAvailableProcessor: PublishProcessor<Boolean> = PublishProcessor.create()
    internal val cameraAvailableFlowable: Flowable<Boolean> = cameraAvailableProcessor

    private val focusedProcessor: PublishProcessor<Boolean> = PublishProcessor.create()
    internal val focusedFlowable: Flowable<Boolean> = focusedProcessor

    override fun onCreate() {
    }

    override fun onResume() {
    }

    override fun onPause() {
    }

    override fun onDestroy() {
    }

    fun onSetCameraAvailableState(b: Boolean) {
        cameraAvailable.set(b)
        cameraAvailableProcessor.onNext(b)
    }

    fun onFocusChanged(b: Boolean) {
        focusedProcessor.onNext(b)
    }

}

enum class Facing(val camera: Int, val description: String, val icon: Int) {
    FRONT(Camera.CameraInfo.CAMERA_FACING_FRONT, "Front camera", R.drawable.ic_camera_front_black_24dp),
    REAR(Camera.CameraInfo.CAMERA_FACING_BACK, "Rear camera", R.drawable.ic_camera_rear_black_24dp)
}