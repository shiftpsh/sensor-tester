package com.shiftpsh.sensortester.camerainfo

import android.databinding.DataBindingUtil
import android.hardware.Camera
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shiftpsh.sensortester.R
import com.shiftpsh.sensortester.databinding.FragmentCameraInfoBinding
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
        }
    }

    override fun onStart() {
        super.onStart()
    }

}
