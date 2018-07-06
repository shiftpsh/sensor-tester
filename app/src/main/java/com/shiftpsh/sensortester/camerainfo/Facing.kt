package com.shiftpsh.sensortester.camerainfo

import android.hardware.Camera
import com.shiftpsh.sensortester.R

enum class Facing(val camera: Int, val description: String, val icon: Int) {
    FRONT(Camera.CameraInfo.CAMERA_FACING_FRONT, "Front camera", R.drawable.ic_camera_front_black_24dp),
    REAR(Camera.CameraInfo.CAMERA_FACING_BACK, "Rear camera", R.drawable.ic_camera_rear_black_24dp)
}