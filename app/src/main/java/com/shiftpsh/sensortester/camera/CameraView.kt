package com.shiftpsh.sensortester.camera

import android.content.Context
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.CaptureResult
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.Surface
import android.view.TextureView
import com.shiftpsh.sensortester.extension.Camera2Extensions
import com.shiftpsh.sensortester.type.Point
import com.shiftpsh.sensortester.type.times
import timber.log.Timber

class CameraView(context: Context, attrs: AttributeSet) : TextureView(context, attrs), TextureView.SurfaceTextureListener {

    var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private lateinit var previewRequestBuilder: CaptureRequest.Builder
    private lateinit var previewRequest: CaptureRequest
    var cameraAvailable = false

    private val captureCallback = object : CameraCaptureSession.CaptureCallback() {
        private fun process(result: CaptureResult) {
        }
    }

    var backgroundThread: HandlerThread
    var backgroundHandler: Handler

    var id: String = ""

    init {
        surfaceTextureListener = this
        backgroundThread = HandlerThread("CameraBackground").also { it.start() }
        backgroundHandler = Handler(backgroundThread.looper)
    }

    fun start(id: String, success: (CameraDevice) -> Unit, callback: () -> Unit) = backgroundHandler.post {
        this.id = id
        Timber.d("CameraView(Camera ID = $id) start")
        try {
            cameraDevice = Camera2Extensions.openCamera(id, handler).second
            cameraAvailable = initialize() || cameraAvailable
            Handler(Looper.getMainLooper()).post {
                try {
                    success(cameraDevice!!)
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

    fun stop()  {
        Timber.d("CameraView(Camera ID = $id) stop")

        cameraDevice?.close()
        cameraDevice = null
    }

    fun capturePicture(after: (ByteArray) -> Unit) {
        /*
        try {
            cameraDevice?.takePicture({}, { _, _ -> }) { byteArray, _ ->
                after(byteArray)
                cameraDevice?.startPreview()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }*/
    }

    private fun initialize(): Boolean {
        val surface = Surface(surfaceTexture)

        previewRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        previewRequestBuilder.addTarget(surface)

        cameraDevice?.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
            override fun onConfigureFailed(session: CameraCaptureSession?) {
                // TODO
            }

            override fun onConfigured(session: CameraCaptureSession?) {
                if (cameraDevice == null) return

                captureSession = session
                setOptions(
                        CaptureRequest.CONTROL_SCENE_MODE to CaptureRequest.CONTROL_SCENE_MODE_HDR,
                        CaptureRequest.CONTROL_AWB_MODE to CaptureRequest.CONTROL_AWB_MODE_AUTO,
                        CaptureRequest.CONTROL_AE_ANTIBANDING_MODE to CaptureRequest.CONTROL_AE_ANTIBANDING_MODE_AUTO,
                        CaptureRequest.CONTROL_EFFECT_MODE to CaptureRequest.CONTROL_EFFECT_MODE_OFF,
                        CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE to CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_ON,
                        CaptureRequest.CONTROL_AF_MODE to CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                )
            }
        }, backgroundHandler)

        return true
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, p1: Int, p2: Int) {
    }

    override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean {
        Timber.d("CameraView(Camera ID = $id) surfaceDestroyed")
        stop()

        return true
    }

    override fun onSurfaceTextureAvailable(p0: SurfaceTexture?, p1: Int, p2: Int) {
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN ||
                event?.action == MotionEvent.ACTION_POINTER_DOWN) {

            val rawCoords = Point(event.getX(0), event.getY(0))
            val viewPosition = Point(left, top)
            val viewSize = Point(width, height)

            val viewCoords = rawCoords - viewPosition

            val focusCoords = 2000 * viewCoords / viewSize - Point(1000, 1000)

            val focusArea = Rect(
                    Math.max(focusCoords.x.toInt() - 100, -1000),
                    Math.max(focusCoords.y.toInt() - 100, -1000),
                    Math.min(focusCoords.x.toInt() + 100, 1000),
                    Math.min(focusCoords.y.toInt() + 100, 1000)
            )
/*
            if (cameraDevice?.parameters?.maxNumFocusAreas ?: 0 > 0) executor.execute {
                cameraDevice?.cancelAutoFocus()
                cameraDevice?.parameters?.apply {
                    focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                    focusAreas = listOf(Camera.Area(focusArea, 1000))

                    if (maxNumMeteringAreas > 0) meteringAreas = focusAreas

                    cameraDevice?.parameters = this

                    Timber.d("Requested focus at: $focusCoords")
                }
            }*/

            return true
        } else return false
    }

    private fun <T> setOptions(vararg options: Pair<CaptureRequest.Key<in T>, T>, postProcess: () -> Unit = {}) {
        for (option in options) {
            previewRequestBuilder[option.first] = option.second
        }
        postProcess()

        previewRequest = previewRequestBuilder.build()
        captureSession?.stopRepeating()
        captureSession?.setRepeatingRequest(previewRequest, captureCallback, backgroundHandler)
    }

}