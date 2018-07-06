package com.shiftpsh.sensortester.camera

import android.content.Context
import android.content.res.Configuration
import android.hardware.Camera
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.shiftpsh.sensortester.camerainfo.Facing
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraView(context: Context, attrs: AttributeSet) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    var camera: Camera? = null
    var facing: Facing = Facing.REAR
    var cameraAvailable = false

    private val executor: ExecutorService by lazy { Executors.newSingleThreadExecutor() }

    init {
        holder.addCallback(this)
    }

    fun start(facing: Facing, callback: (Camera) -> Unit) = executor.execute {
        Timber.d("CameraView($facing) start")
        try {
            camera = Camera.open(facing.camera)
            cameraAvailable = initialize() || cameraAvailable
            Handler(Looper.getMainLooper()).post {
                callback(camera!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            cameraAvailable = false
        }
    }

    fun stop() = executor.execute {
        Timber.d("CameraView($facing) stop")

        camera?.stopPreview()
        camera?.release()
        camera = null
    }

    private fun initialize(): Boolean {
        with(camera ?: return false) {
            val parameters = parameters

            if (resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "portrait")
                parameters.setRotation(90)
                setDisplayOrientation(90)
            } else {
                parameters.set("orientation", "landscape")
                parameters.setRotation(0)
                setDisplayOrientation(0)
            }

            this.parameters = parameters
            setPreviewDisplay(holder)
            startPreview()

            Timber.d("CameraView($facing) init")
        }

        return true
    }

    override fun surfaceCreated(p0: SurfaceHolder?) {
        Timber.d("CameraView($facing) surfaceCreated")
    }

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) = executor.execute {
        Timber.d("CameraView($facing) surfaceDestroyed")

        try {
            camera?.stopPreview()
            camera?.release()
            camera = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        camera = null
    }
}