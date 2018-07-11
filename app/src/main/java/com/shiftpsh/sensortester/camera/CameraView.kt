package com.shiftpsh.sensortester.camera

import android.content.Context
import android.content.res.Configuration
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import com.shiftpsh.sensortester.camerainfo.Facing
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraView(context: Context, attrs: AttributeSet) : TextureView(context, attrs), TextureView.SurfaceTextureListener {

    var camera: Camera? = null
    var facing: Facing = Facing.REAR
    var cameraAvailable = false

    private val executor: ExecutorService by lazy { Executors.newSingleThreadExecutor() }

    init {
        surfaceTextureListener = this
    }

    fun start(facing: Facing, success: (Camera) -> Unit, callback: () -> Unit) = executor.execute {
        Timber.d("CameraView($facing) start")
        try {
            camera = Camera.open(facing.camera)
            cameraAvailable = initialize() || cameraAvailable
            Handler(Looper.getMainLooper()).post {
                try {
                    success(camera!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            cameraAvailable = false
        }
        callback()
    }

    fun stop() = executor.execute {
        Timber.d("CameraView($facing) stop")

        camera?.stopPreview()
        camera?.release()
        camera = null
    }

    fun setPreviewSize(w: Int, h: Int) = executor.execute {
        try {
            camera?.stopPreview()
            camera?.parameters?.apply {
                setPreviewSize(w, h)
                camera?.parameters = this
            }
            camera?.startPreview()
        } catch (e: Exception) {
            e.printStackTrace()
            camera?.startPreview()
        }
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
            setPreviewTexture(surfaceTexture)
            startPreview()

            Timber.d("CameraView($facing) init")
        }

        return true
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, p1: Int, p2: Int) = executor.execute {
    }

    override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean {
        Timber.d("CameraView($facing) surfaceDestroyed")
        stop()

        return true
    }

    override fun onSurfaceTextureAvailable(p0: SurfaceTexture?, p1: Int, p2: Int) {
    }

}