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

class CameraProperty(val property: DefaultCameraProperty, var value: String, val description: String = "", var modified: Boolean = false, val details: List<String>? = listOf()) {

    fun click(context: Context, viewModel: CameraPropertyViewModel) {
        if (details == null) return
        if (details.isEmpty()) return

        val builder = AlertDialog.Builder(context)
        builder.setAdapter(ArrayAdapter(context, android.R.layout.simple_list_item_1, details), { dialogInterface, i ->
            viewModel.onCameraPropertiesChange(details[i])
        })
        builder.setTitle(property.key)
        builder.setIcon(property.icon)
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
                            .let { "up to %.2f fps".format(it[1] / 1000.0f) } else "",
                    it.formatSize("mode", "modes"),
                    false,
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
                    if (it != null) if (it.isNotEmpty()) it.maxBy { it.height * it.width }.let { "up to ${it!!.width} × ${it.height}" } else "" else "",
                    it.formatSize("size", "sizes"),
                    details = it?.map { "${it!!.width} × ${it.height}" }
            )
        }

        temp += supportedPictureSizes.let {
            CameraProperty(
                    DefaultCameraProperty.SIZES_PICTURE,
                    if (it != null) if (it.isNotEmpty()) it.maxBy { it.height * it.width }.let { "up to ${it!!.width} × ${it.height}" } else "" else "",
                    it.formatSize("size", "sizes"),
                    details = it?.map { "${it!!.width} × ${it.height}" }
            )
        }

        temp += supportedVideoSizes.let {
            CameraProperty(
                    DefaultCameraProperty.SIZES_VIDEO,
                    if (it != null) if (it.isNotEmpty()) it.maxBy { it.height * it.width }.let { "up to ${it!!.width} × ${it.height}" } else "" else "",
                    it.formatSize("size", "sizes"),
                    details = it?.map { "${it!!.width} × ${it.height}" }
            )
        }

        temp += supportedSceneModes.let {
            CameraProperty(
                    DefaultCameraProperty.SCENE_MODES,
                    it.formatSize("mode", "modes"),
                    it.examples(),
                    false,
                    it
            )
        }

        temp += supportedWhiteBalance.let {
            CameraProperty(
                    DefaultCameraProperty.WHITEBALANCES,
                    it.formatSize("mode", "modes"),
                    it.examples(),
                    false,
                    it
            )
        }

        temp += supportedFlashModes.let {
            CameraProperty(
                    DefaultCameraProperty.FLASH_MODES,
                    it.formatSize("mode", "modes"),
                    it.examples(),
                    false,
                    it
            )
        }

        temp += supportedAntibanding.let {
            CameraProperty(
                    DefaultCameraProperty.ANTIBANDING,
                    it.formatSize("mode", "modes"),
                    it.examples(),
                    false,
                    it
            )
        }

        temp += supportedColorEffects.let {
            CameraProperty(
                    DefaultCameraProperty.EFFECTS_COLOR,
                    it.formatSize("effect", "effects"),
                    it.examples(),
                    false,
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
                    if (it != null) if (it.isNotEmpty()) "up to ×%.2f".format(it.max()!! / 100.0f) else "" else "",
                    it.formatSize("step", "steps"),
                    false,
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