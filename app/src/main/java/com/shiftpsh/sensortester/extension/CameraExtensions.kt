package com.shiftpsh.sensortester.extension

fun calculateCameraRotation(cameraId: Int): Int {
    val info = android.hardware.Camera.CameraInfo()
    android.hardware.Camera.getCameraInfo(cameraId, info)

    return info.orientation
}