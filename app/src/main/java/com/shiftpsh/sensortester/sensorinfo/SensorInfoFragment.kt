package com.shiftpsh.sensortester.sensorinfo

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.databinding.ObservableField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
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
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SensorInfoFragment : Fragment() {

    lateinit var viewModel: SensorInfoViewModel
    val observables: ArrayList<Pair<SensorType, ObservableField<SensorFormat>>> = arrayListOf()
    val sensorListeners: ArrayList<Pair<SensorType, SensorEventListener>> = arrayListOf()
    private val executor: ExecutorService by lazy { Executors.newSingleThreadExecutor() }

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
                            val apiVersion = Build.VERSION.SDK_INT
                            val property = get()!!
                            if (get()?.type != SensorType.NULL) {
                                removeOnPropertyChangedCallback(this)

                                if (property.type.apiLevel <= apiVersion) {
                                    val sensor = sensorManager.getDefaultSensor(property.type.id)
                                    if (sensor == null) {
                                        set(SensorProperty(property.type, "unsupported", false))
                                    } else {
                                        val listener = object : SensorEventListener {
                                            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                                            }

                                            override fun onSensorChanged(p0: SensorEvent?) {
                                                p0 ?: return
                                                set(SensorProperty(property.type, SensorFormat.format(p0, property.type.type, property.type.unit), true))
                                            }
                                        }

                                        sensorListeners += property.type to listener
                                        executor.execute {
                                            sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
                                        }
                                    }
                                } else {
                                    set(SensorProperty(property.type, "API ${property.type.apiLevel} needed", false))
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

    // FIXME onPause() / onResume() takes so long: lags when orientation changes
    override fun onPause() {
        super.onPause()

        executor.execute {
            val sensorManager = context!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            for (listener in sensorListeners) {
                sensorManager.unregisterListener(listener.second)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        executor.execute {
            val sensorManager = context!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            for (listener in sensorListeners) {
                val sensor = sensorManager.getDefaultSensor(listener.first.id)
                sensorManager.registerListener(listener.second, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }
}