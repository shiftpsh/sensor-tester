package com.shiftpsh.sensortester.sensorinfo

/*
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
                                        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
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
        */