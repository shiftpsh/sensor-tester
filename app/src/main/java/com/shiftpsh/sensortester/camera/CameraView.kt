package com.shiftpsh.sensortester.camera

import android.content.Context
import android.content.res.Configuration
import android.hardware.Camera
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.shiftpsh.sensortester.camerainfo.Facing
import timber.log.Timber

class CameraView(context: Context, attrs: AttributeSet) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    var camera: Camera? = null
    var facing: Facing = Facing.REAR
        set(f) {
            field = f

            camera?.stopPreview()
            camera?.release()

            camera = Camera.open(facing.camera)
            camera?.startPreview()
        }
    var cameraAvailable = false

    init {
        holder.addCallback(this)
    }

    fun start(): Boolean {
        Timber.d("CameraView start")
        try {
            camera = Camera.open(facing.camera)
        } catch (e: Exception) {
            e.printStackTrace()
            cameraAvailable = false
            return false
        }

        cameraAvailable = initialize() || cameraAvailable

        return cameraAvailable
    }

    fun stop() {
        Timber.d("CameraView stop")

        camera?.stopPreview()
        camera?.release()
        camera = null
    }

    fun initialize(): Boolean {
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
        }

        return true
    }

    override fun surfaceCreated(p0: SurfaceHolder?) {
        Timber.d("CameraView surfaceCreated")

        initialize()
    }

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        Timber.d("CameraView surfaceDestroyed")

        with(camera ?: return) {
            stopPreview()
            release()
            camera = null
        }
    }

}