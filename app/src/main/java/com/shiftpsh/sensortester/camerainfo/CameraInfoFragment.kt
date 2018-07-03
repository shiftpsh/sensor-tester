package com.shiftpsh.sensortester.camerainfo

import android.databinding.DataBindingUtil
import android.hardware.Camera
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shiftpsh.sensortester.BaseRecyclerViewAdapter
import com.shiftpsh.sensortester.R
import com.shiftpsh.sensortester.camerainfo.item.CameraProperty
import com.shiftpsh.sensortester.camerainfo.item.CameraPropertyViewModel
import com.shiftpsh.sensortester.databinding.FragmentCameraInfoBinding
import com.shiftpsh.sensortester.databinding.ItemPropertiesBinding
import com.shiftpsh.sensortester.extension.calculateCameraRotation
import com.shiftpsh.sensortester.extension.examples
import com.shiftpsh.sensortester.extension.formatNumber
import com.shiftpsh.sensortester.extension.formatSize
import kotlinx.android.synthetic.main.fragment_camera_info.*
import timber.log.Timber

class CameraInfoFragment : Fragment() {

    lateinit var viewModel: CameraInfoViewModel
    lateinit var camera: Camera
    lateinit var facing: Facing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val facingRaw = arguments?.getString("facing")
        facing = Facing.valueOf(facingRaw ?: Facing.FRONT.name)
        Timber.d(facingRaw)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel = CameraInfoViewModel()

        val inflation = DataBindingUtil.inflate<FragmentCameraInfoBinding>(
                inflater, R.layout.fragment_camera_info, container, false
        ).apply {
            vm = viewModel
        }

        return inflation.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        lifecycle.addObserver(viewModel)

        viewModel.cameraFacing.set(facing)

        try {
            camera = Camera.open(facing.camera)
            viewModel.cameraAvailable.set(true)
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }

        ui_properties.adapter = object : BaseRecyclerViewAdapter<CameraProperty, CameraPropertyViewModel>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<CameraProperty, CameraPropertyViewModel> {
                val itemView = LayoutInflater.from(context)
                        .inflate(R.layout.item_properties, parent, false)

                val viewModel = CameraPropertyViewModel()

                val binding = ItemPropertiesBinding.bind(itemView)
                binding.vm = viewModel

                return ItemViewHolder(itemView, binding, viewModel)
            }
        }.apply {
            val parameters = camera.parameters!!
            val temp = ArrayList<CameraProperty>()

            // TODO click to reveal advanced information
            with(parameters) {
                temp += CameraProperty(
                        "Rotation",
                        "${calculateCameraRotation(facing.camera)}°",
                        "",
                        R.drawable.ic_rotate_90_degrees_ccw_black_24dp,
                        {})

                temp += supportedPreviewSizes.let {
                    CameraProperty(
                            "Preview Sizes",
                            it.formatSize("size", "sizes"),
                            if (it.isNotEmpty()) "up to ${it.maxBy { it.height * it.width }.let { "${it!!.height} × ${it.width}" }}" else "",
                            R.drawable.ic_photo_size_select_large_black_24dp,
                            {})
                }

                temp += supportedPictureSizes.let {
                    CameraProperty(
                            "Picture Sizes",
                            it.formatSize("size", "sizes"),
                            if (it.isNotEmpty()) "up to ${it.maxBy { it.height * it.width }.let { "${it!!.height} × ${it.width}" }}" else "",
                            R.drawable.ic_photo_size_select_large_black_24dp,
                            {})
                }

                temp += supportedSceneModes.let {
                    CameraProperty(
                            "Scene Modes",
                            it.formatSize("mode", "modes"),
                            it.examples(),
                            R.drawable.ic_panorama_horizontal_black_24dp,
                            {}
                    )
                }

                temp += supportedWhiteBalance.let {
                    CameraProperty(
                            "Whitebalances",
                            it.formatSize("mode", "modes"),
                            it.examples(),
                            R.drawable.ic_wb_auto_black_24dp,
                            {}
                    )
                }

                temp += supportedFlashModes.let {
                    CameraProperty(
                            "Flash Modes",
                            it.formatSize("mode", "modes"),
                            it.examples(),
                            R.drawable.ic_flash_on_black_24dp,
                            {}
                    )
                }

                temp += supportedAntibanding.let {
                    CameraProperty(
                            "Antibanding",
                            it.formatSize("mode", "modes"),
                            it.examples(),
                            R.drawable.ic_burst_mode_black_24dp,
                            {}
                    )
                }

                temp += supportedColorEffects.let {
                    CameraProperty(
                            "Color Effects",
                            it.formatSize("effect", "effects"),
                            it.examples(),
                            R.drawable.ic_color_lens_black_24dp,
                            {}
                    )
                }

                temp += supportedPreviewFpsRange.let {
                    CameraProperty(
                            "Preview FPS",
                            it.formatSize("mode", "modes"),
                            if (it.isNotEmpty()) "up to ${it.maxBy { it[0] + it[1] }!!
                                    .let { "%.3f .. %.3f fps".format(it[0]/1000.0f, it[1]/1000.0f) }}" else "",
                            R.drawable.ic_burst_mode_black_24dp,
                            {}
                    )
                }

                temp += isVideoStabilizationSupported.let {
                    CameraProperty(
                            "Video Stabilization",
                            if (it) "supported" else "unsupported",
                            "",
                            R.drawable.ic_leak_remove_black_24dp,
                            {}
                    )
                }

                temp += maxNumFocusAreas.let {
                    CameraProperty(
                            "Max Focus Points",
                            it.formatNumber("point", "points"),
                            "",
                            R.drawable.ic_center_focus_weak_black_24dp,
                            {}
                    )
                }

                temp += maxNumMeteringAreas.let {
                    CameraProperty (
                            "Max Metering Modes",
                            it.formatNumber("mode", "modes"),
                            "",
                            R.drawable.ic_settings_overscan_black_24dp,
                            {}
                    )
                }

                temp += isZoomSupported.let {
                    CameraProperty (
                            "Zoom",
                            if (it) "supported" else "unsupported",
                            "",
                            R.drawable.ic_zoom_in_black_24dp,
                            {}
                    )
                }

                temp += zoomRatios.let {
                    CameraProperty (
                            "Zoom Ratio",
                            it.formatSize("step", "steps"),
                            if (it.isNotEmpty()) "up to ×%.2f".format(it.max()!! / 100.0f) else "",
                            R.drawable.ic_zoom_in_black_24dp,
                            {}
                    )
                }
            }

            items = temp
        }
    }

    override fun onStart() {
        super.onStart()
    }

}
