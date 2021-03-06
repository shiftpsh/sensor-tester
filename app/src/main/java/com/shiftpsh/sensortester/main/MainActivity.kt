package com.shiftpsh.sensortester.main

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.shiftpsh.sensortester.BaseRecyclerViewAdapter
import com.shiftpsh.sensortester.R
import com.shiftpsh.sensortester.camerainfo.item.CameraProperty
import com.shiftpsh.sensortester.camerainfo.item.CameraPropertyViewModel
import com.shiftpsh.sensortester.camerainfo.item.DefaultCameraProperty
import com.shiftpsh.sensortester.camerainfo.item.getProperties
import com.shiftpsh.sensortester.databinding.ActivityMainBinding
import com.shiftpsh.sensortester.databinding.ItemCameraPropertiesBinding
import com.shiftpsh.sensortester.databinding.ItemSensorPropertiesBinding
import com.shiftpsh.sensortester.extension.PICTURES_DIRECTORY
import com.shiftpsh.sensortester.extension.makeFloat
import com.shiftpsh.sensortester.extension.requestPermissions
import com.shiftpsh.sensortester.extension.toast
import com.shiftpsh.sensortester.sensorinfo.item.*
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel
    var lastPage = 0

    val sensorListeners: ArrayList<Pair<SensorType, SensorEventListener>> = arrayListOf()

    val cameraAdapter = object : BaseRecyclerViewAdapter<CameraProperty, CameraPropertyViewModel>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<CameraProperty, CameraPropertyViewModel> {
            val itemView = LayoutInflater.from(this@MainActivity)
                    .inflate(R.layout.item_camera_properties, parent, false)

            val viewModel = CameraPropertyViewModel()

            val binding = ItemCameraPropertiesBinding.bind(itemView)
            binding.vm = viewModel

            viewModel.cameraPropertiesFlowable.subscribe {
                with(ui_camera_preview.camera?.parameters ?: return@subscribe) {
                    when (it.first) {
                        DefaultCameraProperty.PREVIEW_FPS -> {
                            val value = it.second.split("..").map { it.makeFloat() }
                            setPreviewFpsRange((value[0] * 1000).toInt(), (value[1] * 1000).toInt())
                        }
                        DefaultCameraProperty.SIZES_PREVIEW -> {
                            val value = it.second.split("×").map { it.makeFloat().roundToInt() }
                            this@MainActivity.viewModel.aspectRatio.set("${value[0]}:${value[1]}")
                            ui_camera_preview.setPreviewSize(value[0], value[1])
                        }
                        DefaultCameraProperty.SIZES_PICTURE -> {
                            val value = it.second.split("×").map { it.makeFloat().roundToInt() }
                            ui_camera_preview.setPictureSize(value[0], value[1])
                        }
                        DefaultCameraProperty.SCENE_MODES -> {
                            sceneMode = it.second
                        }
                        DefaultCameraProperty.WHITEBALANCES -> {
                            whiteBalance = it.second
                        }
                        DefaultCameraProperty.FLASH_MODES -> {
                            flashMode = it.second
                        }
                        DefaultCameraProperty.ANTIBANDING -> {
                            antibanding = it.second
                        }
                        DefaultCameraProperty.EFFECTS_COLOR -> {
                            colorEffect = it.second
                        }
                        DefaultCameraProperty.ZOOM_RATIO -> {
                            val value = it.second.makeFloat() * 100
                            zoom = zoomRatios.indexOf(value.roundToInt())
                        }
                    }

                    setParameters(this)
                }
                notifyDataSetChanged()
            }

            return ItemViewHolder(itemView, binding, viewModel)
        }
    }

    fun sensorAdapter(sensorManager: SensorManager) = object : BaseRecyclerViewAdapter<SensorProperty, SensorPropertyViewModel>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<SensorProperty, SensorPropertyViewModel> {
            val itemView = LayoutInflater.from(this@MainActivity)
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
    }

    fun setParameters(parameters: android.hardware.Camera.Parameters) {
        ui_camera_preview.camera?.parameters = parameters
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = MainViewModel()
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            vm = viewModel
        }

        val initialIndex = savedInstanceState?.getInt("index") ?: 0
        lastPage = initialIndex

        setSupportActionBar(ui_toolbar)
        requestPermissions(listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ) { initialize(initialIndex) }
        lifecycle.addObserver(viewModel)
    }

    fun initialize(initialIndex: Int) {
        Timber.d("MainActivity init")

        viewModel.currentPageFlowable.subscribe {
            lastPage = it
        }

        viewModel.stateFlowable.subscribe { (isCurrentTypeCamera, facing) ->
            if (isCurrentTypeCamera) {
                viewModel.onCameraWaitingStateChanged(true)
                viewModel.onCameraAvailiabilityChanged(false)
                ui_camera_preview.stop()

                ui_camera_preview.start(facing, {
                    ui_properties.adapter = cameraAdapter.apply {
                        items = it.getProperties(facing)
                    }
                    ui_camera_preview.camera?.parameters?.previewSize?.let {
                        viewModel.aspectRatio.set("${it.width}:${it.height}")
                    }
                    viewModel.onCameraAvailiabilityChanged(true)
                }, {
                    viewModel.onCameraWaitingStateChanged(false)
                })
            } else {
                val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

                sensorAdapter(sensorManager).let {
                    it.items = getSensorProperties()
                    ui_properties.adapter = it
                }
            }
        }

        viewModel.captureFlowable.subscribe {
            ui_camera_preview.capturePicture { data ->
                if (!PICTURES_DIRECTORY.exists() && !PICTURES_DIRECTORY.mkdirs()) {
                    toast("Capture failed!")

                    return@capturePicture
                }

                val dateFormat = SimpleDateFormat("yyyymmdd_hhmmss", Locale.US)
                val date = dateFormat.format(Date())
                val photoFile = "Picture_$date.jpg"

                val filename = PICTURES_DIRECTORY.path + File.separator + photoFile

                var index = 0
                var pictureFile = File(filename)

                while (pictureFile.exists()) {
                    pictureFile = File("${photoFile}_$index")
                    index++
                }

                try {
                    val fos = FileOutputStream(pictureFile)
                    fos.write(data)
                    fos.close()

                    val image = ContentValues()
                    image.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

                    val path = pictureFile.parentFile.toString().toLowerCase()
                    val name = pictureFile.parentFile.name.toLowerCase()

                    image.put(MediaStore.Images.ImageColumns.BUCKET_ID, path.hashCode())
                    image.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, name)
                    image.put(MediaStore.Images.Media.SIZE, pictureFile.length())

                    image.put(MediaStore.Images.Media.DATA, pictureFile.absolutePath)

                    val result = baseContext.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image)

                    toast("Image saved: $photoFile")
                } catch (e: Exception) {
                    e.printStackTrace()

                    toast("Capture failed!")
                }

            }
        }

        viewModel.onCurrentPageChanged(initialIndex, true)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putInt("index", lastPage)
        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        if (lastPage < 2) {
            ui_camera_preview.stop()
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.onCurrentPageChanged(lastPage, true)
    }
}
