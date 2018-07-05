package com.shiftpsh.sensortester.camerainfo

import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
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
import com.shiftpsh.sensortester.camerainfo.item.getProperties
import com.shiftpsh.sensortester.databinding.FragmentCameraInfoBinding
import com.shiftpsh.sensortester.databinding.ItemCameraPropertiesBinding
import com.shiftpsh.sensortester.extension.onPropertyChanged
import kotlinx.android.synthetic.main.fragment_camera_info.*
import timber.log.Timber

class CameraInfoFragment : Fragment() {

    lateinit var viewModel: CameraInfoViewModel
    lateinit var camera: Camera
    lateinit var facing: Facing

    val focused = ObservableBoolean(true)

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
                        .inflate(R.layout.item_camera_properties, parent, false)

                val viewModel = CameraPropertyViewModel()

                val binding = ItemCameraPropertiesBinding.bind(itemView)
                binding.vm = viewModel

                return ItemViewHolder(itemView, binding, viewModel)
            }
        }.apply {
            items = camera.getProperties(facing)
        }

        ui_camera_preview.facing = facing.camera

        focused.onPropertyChanged { sender, propertyId ->
            try {
                if (focused.get()) {
                    ui_camera_preview.start()
                } else {
                    ui_camera_preview.stop()
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }

        if (focused.get()) ui_camera_preview.start()
    }

    override fun onResume() {
        super.onResume()
        if (focused.get()) ui_camera_preview.start()
    }

    override fun onPause() {
        super.onPause()
        ui_camera_preview.stop()
    }

}
