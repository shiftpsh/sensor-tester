package com.shiftpsh.sensortester.extension

import android.app.Application
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.params.StreamConfigurationMap
import android.os.Handler
import com.shiftpsh.sensortester.camerainfo.Facing
import timber.log.Timber
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

object Camera2Extensions {

    internal lateinit var cameraManager: CameraManager
        private set

    fun init(application: Application) {
        cameraManager = application.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    /**
     * Returns Camera with Rotation in Degrees
     */
    @Throws(SecurityException::class)
    fun openCamera(id: String, backgroundHandler: Handler): Pair<Int, CameraDevice> {
        val cameraOpenLock = Semaphore(1)
        cameraOpenLock.acquire()

        var cameraDevice: CameraDevice? = null

        val callback = object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice?) {
                cameraDevice = camera
                cameraOpenLock.release()
            }

            override fun onDisconnected(camera: CameraDevice?) {
            }

            override fun onError(camera: CameraDevice?, error: Int) {
            }
        }

        fun processCameraId(id: String): Pair<Int, CameraDevice> {
            cameraManager.openCamera(id, callback, backgroundHandler)

            if (!cameraOpenLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw RuntimeException("Time out waiting to lock cameraDevice opening.")
            }

            return Pair(calculateCameraRotation(id), cameraDevice!!)
        }

        return processCameraId(id)
    }

    private fun calculateCameraRotation(cameraId: String): Int {
        val info = cameraManager.getCameraCharacteristics(cameraId)
        val result: Int = info[CameraCharacteristics.SENSOR_ORIENTATION]

        Timber.d("Rotation : %d", result)

        return result
    }

    fun availableCameraCharacteristics(): List<Pair<String, CameraCharacteristics>> {
        return cameraManager.cameraIdList.map {
            it to cameraManager.getCameraCharacteristics(it)
        }
    }

    fun availableCameraIdFacings(): List<Pair<String, Facing>> {
        return availableCameraCharacteristics().map {
            val facing = when (it.second[CameraCharacteristics.LENS_FACING]) {
                CameraCharacteristics.LENS_FACING_FRONT -> Facing.FRONT
                CameraCharacteristics.LENS_FACING_BACK -> Facing.REAR
                else -> Facing.UNKNOWN
            }
            it.first to facing
        }
    }
}

val CameraDevice.characteristics: CameraCharacteristics
    get() = Camera2Extensions.cameraManager.getCameraCharacteristics(id)
val CameraCharacteristics.streamConfigurationMap: StreamConfigurationMap
    get() = get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
val CameraDevice.streamConfigurationMap: StreamConfigurationMap
    get() = characteristics.streamConfigurationMap