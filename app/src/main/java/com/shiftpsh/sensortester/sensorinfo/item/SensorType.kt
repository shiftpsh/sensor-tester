package com.shiftpsh.sensortester.sensorinfo.item

import android.hardware.Sensor
import android.hardware.SensorEvent
import com.shiftpsh.sensortester.R

enum class SensorDataType(val argCount: Int, val defaultFormat: String) {
    VECTOR3(
            3, "x = %.2f[unit]\ny = %.2f[unit]\nz = %.2f[unit]"
    ),
    VECTOR_ROTATION(
            3, "xsin(θ/2) = %.2f[unit]\nysin(θ/2) = %.2f[unit]\nzsin(θ/2) = %.2f[unit]"
    ),
    ORIENTATION(
            3, "azimuth = %.2f[unit]\npitch = %.2f[unit]\nroll = %.2f[unit]"
    ),
    SCALAR(
            1, "%.2f[unit]"
    ),
    SCALAR_INT(
            1, "%d[unit]"
    ),
    SCALAR_PERCENT(
            1, "%.2f[unit]"
    );
}

class SensorFormat {
    companion object {
        fun format(event: SensorEvent, type: SensorDataType, unit: String) = type.defaultFormat
                .let {
                    with(event) {
                        when (type) {
                            SensorDataType.VECTOR3,
                            SensorDataType.VECTOR_ROTATION,
                            SensorDataType.ORIENTATION -> it.format(values[0], values[1], values[2])
                            SensorDataType.SCALAR -> it.format(values[0])
                            SensorDataType.SCALAR_INT -> it.format(values[0].toInt())
                            SensorDataType.SCALAR_PERCENT -> it.format(values[0] * 100)
                        }
                    }
                }.replace("[unit]", if (unit.isEmpty()) "" else " $unit")
    }
}

enum class SensorType(val id: Int, val apiLevel: Int, val representation: String, val icon: Int, val type: SensorDataType, val unit: String = "") {
    NULL(0, 0, "", 0, SensorDataType.SCALAR, ""),
    ACCELEROMETER(Sensor.TYPE_ACCELEROMETER, 20,
            "Accelerometer", R.drawable.ic_sensor_black_24dp, SensorDataType.VECTOR3, "m/s²"),
    GRAVITY(Sensor.TYPE_GRAVITY, 20,
            "Gravity", R.drawable.ic_public_black_24dp, SensorDataType.VECTOR3, "m/s²"),
    GYROSCOPE(Sensor.TYPE_GYROSCOPE, 20,
            "Gyroscope", R.drawable.ic_3d_rotation_black_24dp, SensorDataType.VECTOR3, "rad/s"),
    HEART_BEAT(Sensor.TYPE_HEART_BEAT, 24,
            "Heartbeat Confidence", R.drawable.ic_favorite_black_24dp, SensorDataType.SCALAR_PERCENT, "%"),
    HEART_RATE(Sensor.TYPE_HEART_RATE, 20,
            "Heart Rate", R.drawable.ic_favorite_black_24dp, SensorDataType.SCALAR, "bpm"),
    HUMIDITY_RELATIVE(Sensor.TYPE_RELATIVE_HUMIDITY, 14,
            "Relative Humidity", R.drawable.ic_invert_colors_black_24dp, SensorDataType.SCALAR, "%"),
    LIGHT(Sensor.TYPE_LIGHT, 3,
            "Light", R.drawable.ic_lightbulb_outline_black_24dp, SensorDataType.SCALAR, "lux"),
    LINEAR_ACCELERATION(Sensor.TYPE_LINEAR_ACCELERATION, 9,
            "Linear Acceleration", R.drawable.ic_call_made_black_24dp, SensorDataType.VECTOR3, "m/s²"),
    MAGNETIC_FIELD(Sensor.TYPE_MAGNETIC_FIELD, 3,
            "Magnetic Field", R.drawable.ic_leak_add_black_24dp, SensorDataType.VECTOR3, "µT"),
    // MOTION_DETECT
    ORIENTATION(Sensor.TYPE_ORIENTATION, 3,
            "Orientation", R.drawable.ic_screen_rotation_black_24dp, SensorDataType.ORIENTATION),
    // POSE_6DOF
    PRESSURE(Sensor.TYPE_PRESSURE, 3,
            "Pressure", R.drawable.ic_fullscreen_exit_black_24dp, SensorDataType.SCALAR, "hPa"),
    PROXIMITY(Sensor.TYPE_PROXIMITY, 3,
            "Proximity", R.drawable.ic_settings_ethernet_black_24dp, SensorDataType.SCALAR, "cm"),
    ROTATION_VECTOR(Sensor.TYPE_ROTATION_VECTOR, 9,
            "Rotation Vector", R.drawable.ic_3d_rotation_black_24dp, SensorDataType.VECTOR_ROTATION),
    ROTATION_VECTOR_GEOMAGNETIC(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR, 20,
            "Geomagnetic Rotation Vector", R.drawable.ic_3d_rotation_black_24dp, SensorDataType.VECTOR_ROTATION),
    STEP_COUNTER(Sensor.TYPE_STEP_COUNTER, 19,
            "Step Counter", R.drawable.ic_directions_walk_black_24dp, SensorDataType.SCALAR_INT, "steps"),
    // STATIONARY_DETECT
    // STEP_DETECTOR
    TEMPERATURE(Sensor.TYPE_TEMPERATURE, 3,
            "Temperature", R.drawable.ic_ac_unit_black_24dp, SensorDataType.SCALAR, "°C"),
    TEMPERATURE_AMBIENT(Sensor.TYPE_AMBIENT_TEMPERATURE, 20,
            "Ambient Temperature", R.drawable.ic_ac_unit_black_24dp, SensorDataType.SCALAR, "°C")
}