package com.shiftpsh.sensortester.camerainfo.item

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.hardware.Camera
import android.widget.ArrayAdapter
import com.shiftpsh.sensortester.R
import com.shiftpsh.sensortester.camerainfo.Facing
import com.shiftpsh.sensortester.extension.calculateCameraRotation
import com.shiftpsh.sensortester.extension.examples
import com.shiftpsh.sensortester.extension.formatNumber
import com.shiftpsh.sensortester.extension.formatSize
import timber.log.Timber

class CameraProperty(val key: String, val icon: Int, val value: String, val description: String = "", val details: List<String>? = listOf()) {
    constructor(property: DefaultCameraProperty, value: String, description: String = "", details: List<String>? = listOf())
            : this(property.key, property.icon, value, description, details)

    fun click (context: Context) {
        if (details == null) return
        if (details.isEmpty()) return

        val builder = AlertDialog.Builder(context)
        builder.setAdapter(ArrayAdapter(context, android.R.layout.simple_list_item_1, details), DialogInterface.OnClickListener { dialogInterface, i ->

        })
        builder.setTitle(key)
        builder.setIcon(icon)
        builder.setCancelable(true)

        builder.create().show()
    }
}

enum class DefaultCameraProperty(val key: String, val icon: Int) {
    ROTATION("Rotation", R.drawable.ic_rotate_90_degrees_ccw_black_24dp),
    PREVIEW_FPS("Preview FPS", R.drawable.ic_burst_mode_black_24dp),
    VIEW_ANGLE_HORIZONTAL("Horizontal View Angle", R.drawable.ic_looks_black_24dp),
    VIEW_ANGLE_VERTICAL("Vertical View Angle", R.drawable.ic_looks_black_24dp),
    SIZES_PREVIEW("Preview Size", R.drawable.ic_photo_size_select_large_black_24dp),
    SIZES_PICTURE("Picture Size", R.drawable.ic_photo_size_select_large_black_24dp),
    SIZES_VIDEO("Video Size", R.drawable.ic_videocam_black_24dp),
    SCENE_MODES("Scene Modes", R.drawable.ic_panorama_horizontal_black_24dp),
    WHITEBALANCES("Whitebalances", R.drawable.ic_wb_auto_black_24dp),
    FLASH_MODES("Flash Modes", R.drawable.ic_flash_on_black_24dp),
    ANTIBANDING("Antibanding", R.drawable.ic_burst_mode_black_24dp),
    EFFECTS_COLOR("Color Effects", R.drawable.ic_color_lens_black_24dp),
    ZOOM("Zoom", R.drawable.ic_zoom_in_black_24dp),
    ZOOM_RATIO("Zoom Ratio", R.drawable.ic_zoom_in_black_24dp),
    VIDEO_STABILIZATION("Video Stabilization", R.drawable.ic_leak_remove_black_24dp),
    FOCUS_POINTS("Focus Points", R.drawable.ic_center_focus_weak_black_24dp),
    METERING_MODES("Metering Modes", R.drawable.ic_settings_overscan_black_24dp)
}

fun Camera.getProperties(facing: Facing): ArrayList<CameraProperty> {
    val parameters = parameters!!
    val temp = ArrayList<CameraProperty>()

    // TODO click to reveal advanced information
    with(parameters) {
        temp += CameraProperty(
                DefaultCameraProperty.ROTATION,
                "${calculateCameraRotation(facing.camera)}°"
        )

        temp += supportedPreviewFpsRange.let {
            CameraProperty(
                    DefaultCameraProperty.PREVIEW_FPS,
                    if (it.isNotEmpty()) it.maxBy { it[1] }!!
                            .let { "%.2f fps".format(it[1] / 1000.0f) } else "",
                    it.formatSize("mode", "modes"),
                    it.map { "%.2f .. %.2f fps".format(it[0] / 1000.0f, it[1] / 1000.0f) }
            )
        }

        temp += horizontalViewAngle.let {
            CameraProperty(
                    DefaultCameraProperty.VIEW_ANGLE_HORIZONTAL,
                    if (it != -1.0f) "%.1f°".format(it) else "-"
            )
        }

        temp += verticalViewAngle.let {
            CameraProperty(
                    DefaultCameraProperty.VIEW_ANGLE_VERTICAL,
                    if (it != -1.0f) "%.1f°".format(it) else "-"
            )
        }

        temp += supportedPreviewSizes.let {
            CameraProperty(
                    DefaultCameraProperty.SIZES_PREVIEW,
                    if (it != null) if (it.isNotEmpty()) it.maxBy { it.height * it.width }.let { "${it!!.height} × ${it.width}" } else "" else "",
                    it.formatSize("size", "sizes"),
                    details = it?.map { "${it!!.height} × ${it.width}" }
            )
        }

        temp += supportedPictureSizes.let {
            CameraProperty(
                    DefaultCameraProperty.SIZES_PICTURE,
                    if (it != null) if (it.isNotEmpty()) it.maxBy { it.height * it.width }.let { "${it!!.height} × ${it.width}" } else "" else "",
                    it.formatSize("size", "sizes"),
                    details = it?.map { "${it!!.height} × ${it.width}" }
            )
        }

        temp += supportedVideoSizes.let {
            CameraProperty(
                    DefaultCameraProperty.SIZES_VIDEO,
                    if (it != null) if (it.isNotEmpty()) it.maxBy { it.height * it.width }.let { "${it!!.height} × ${it.width}" } else "" else "",
                    it.formatSize("size", "sizes"),
                    details = it?.map { "${it!!.height} × ${it.width}" }
            )
        }

        temp += supportedSceneModes.let {
            CameraProperty(
                    DefaultCameraProperty.SCENE_MODES,
                    it.formatSize("mode", "modes"),
                    it.examples(),
                    it
            )
        }

        temp += supportedWhiteBalance.let {
            CameraProperty(
                    DefaultCameraProperty.WHITEBALANCES,
                    it.formatSize("mode", "modes"),
                    it.examples(),
                    it
            )
        }

        temp += supportedFlashModes.let {
            CameraProperty(
                    DefaultCameraProperty.FLASH_MODES,
                    it.formatSize("mode", "modes"),
                    it.examples(),
                    it
            )
        }

        temp += supportedAntibanding.let {
            CameraProperty(
                    DefaultCameraProperty.ANTIBANDING,
                    it.formatSize("mode", "modes"),
                    it.examples(),
                    it
            )
        }

        temp += supportedColorEffects.let {
            CameraProperty(
                    DefaultCameraProperty.EFFECTS_COLOR,
                    it.formatSize("effect", "effects"),
                    it.examples(),
                    it
            )
        }

        temp += isZoomSupported.let {
            CameraProperty(
                    DefaultCameraProperty.ZOOM,
                    if (it) "supported" else "unsupported"
            )
        }

        temp += zoomRatios.let {
            CameraProperty(
                    DefaultCameraProperty.ZOOM_RATIO,
                    if (it != null) if (it.isNotEmpty()) "×%.2f".format(it.max()!! / 100.0f) else "" else "",
                    it.formatSize("step", "steps"),
                    it?.map { "×%.2f".format(it / 100.0f) }
            )
        }

        temp += isVideoStabilizationSupported.let {
            CameraProperty(
                    DefaultCameraProperty.VIDEO_STABILIZATION,
                    if (it) "supported" else "unsupported"
            )
        }

        temp += maxNumFocusAreas.let {
            CameraProperty(
                    DefaultCameraProperty.FOCUS_POINTS,
                    it.formatNumber("point", "points")
            )
        }

        temp += maxNumMeteringAreas.let {
            CameraProperty(
                    DefaultCameraProperty.METERING_MODES,
                    it.formatNumber("mode", "modes")
            )
        }
    }

    return temp
}