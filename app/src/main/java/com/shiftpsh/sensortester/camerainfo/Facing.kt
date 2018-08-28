package com.shiftpsh.sensortester.camerainfo

import android.hardware.Camera
import android.hardware.camera2.CameraCharacteristics
import com.shiftpsh.sensortester.R

enum class Facing(val camera: Int, val description: String, val icon: Int) {
    FRONT(CameraCharacteristics.LENS_FACING_FRONT, "Front Camera", R.drawable.ic_camera_front_black_24dp),
    REAR(CameraCharacteristics.LENS_FACING_BACK, "Rear Camera", R.drawable.ic_camera_rear_black_24dp),
    UNKNOWN(-1, "Unknown", R.drawable.ic_photo_camera_black_24dp)
}