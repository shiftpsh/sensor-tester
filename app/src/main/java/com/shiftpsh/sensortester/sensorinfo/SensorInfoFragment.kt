package com.shiftpsh.sensortester.sensorinfo

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shiftpsh.sensortester.BaseRecyclerViewAdapter
import com.shiftpsh.sensortester.R
import com.shiftpsh.sensortester.databinding.FragmentSensorInfoBinding
import com.shiftpsh.sensortester.databinding.ItemSensorPropertiesBinding
import com.shiftpsh.sensortester.sensorinfo.item.*
import kotlinx.android.synthetic.main.fragment_sensor_info.*

class SensorInfoFragment : Fragment() {

    lateinit var viewModel: SensorInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel = SensorInfoViewModel()

        val inflation = DataBindingUtil.inflate<FragmentSensorInfoBinding>(
                inflater, R.layout.fragment_sensor_info, container, false
        ).apply {
            vm = viewModel
        }

        return inflation.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        lifecycle.addObserver(viewModel)

        val sensorManager = context!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        object : BaseRecyclerViewAdapter<SensorProperty, SensorPropertyViewModel>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<SensorProperty, SensorPropertyViewModel> {
                val itemView = LayoutInflater.from(context)
                        .inflate(R.layout.item_sensor_properties, parent, false)

                val viewModel = SensorPropertyViewModel()
                val binding = ItemSensorPropertiesBinding.bind(itemView)
                binding.vm = viewModel

                // TODO cleanup dirty code
                with(viewModel.property) {
                    val x = object : Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                            val property = get()!!
                            if (get()?.type != SensorType.NULL) {
                                removeOnPropertyChangedCallback(this)

                                val sensor = sensorManager.getDefaultSensor(property.type.id)
                                if (sensor == null) {
                                    set(SensorProperty(property.type, "unsupported"))
                                } else {
                                    sensorManager.registerListener(object : SensorEventListener {
                                        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                                        }

                                        override fun onSensorChanged(p0: SensorEvent?) {
                                            p0 ?: return
                                            set(SensorProperty(property.type, SensorFormat.format(p0, property.type.type, property.type.unit)))
                                        }
                                    }, sensor, SensorManager.SENSOR_DELAY_NORMAL)
                                }
                            }
                        }
                    }
                    addOnPropertyChangedCallback(x)
                }

                return ItemViewHolder(itemView, binding, viewModel)
            }
        }.let {
            it.items = getSensorProperties()
            ui_properties.adapter = it
        }
    }
}