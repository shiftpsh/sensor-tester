package com.shiftpsh.sensortester.sensorinfo.item

import android.hardware.Sensor
import android.hardware.SensorEvent

enum class SensorDataType(val defaultFormat: (SensorEvent) -> String) {
    VECTOR3({
        "x = %.2f[unit], y = %.2f[unit], z = %.2f[unit]".format(it.values[0], it.values[1], it.values[2])
    }),
    VECTOR_ROTATION({
        "xsin(θ/2) = %.2f[unit], ysin(θ/2) = %.2f[unit], zsin(θ/2) = %.2f[unit]".format(it.values[0], it.values[1], it.values[2])
    }),
    ORIENTATION({
        "azimuth = %.2f[unit], pitch = %.2f[unit], roll = %.2f[unit]".format(it.values[0], it.values[1], it.values[2])
    }),
    SCALAR({
        "%.2f[unit]".format(it.values[0])
    }),
    SCALAR_PERCENT({
        "%.2f[unit]".format(it.values[0] * 100)
    });
}

fun SensorEvent.format(type: SensorDataType, unit: String)
        = type.defaultFormat(this).replace("[unit]", if (unit.isEmpty()) "" else " $unit")

enum class SensorType(val id: Int, val representation: String, val type: SensorDataType, val unit: String = "") {
    ACCELEROMETER(Sensor.TYPE_ACCELEROMETER, "Accelerometer", SensorDataType.VECTOR3, "m/s²"),
    AMBIENT_TEMPERATURE(Sensor.TYPE_AMBIENT_TEMPERATURE, "Ambient Temperature", SensorDataType.SCALAR, "°C"),
    GEOMAGNETIC_ROTATION_VECTOR(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR, "Geomagnetic Rotation Vector", SensorDataType.VECTOR_ROTATION),
    GRAVITY(Sensor.TYPE_GRAVITY, "Gravity", SensorDataType.VECTOR3, "m/s²"),
    GYROSCOPE(Sensor.TYPE_GYROSCOPE, "Gyroscope", SensorDataType.VECTOR3, "rad/s"),
    HEART_BEAT(Sensor.TYPE_HEART_BEAT, "Heartbeat Confidence", SensorDataType.SCALAR_PERCENT, "%"),
    HEART_RATE(Sensor.TYPE_HEART_RATE, "Heart Rate", SensorDataType.SCALAR, "bpm"),
    LIGHT(Sensor.TYPE_LIGHT, "Light", SensorDataType.SCALAR, "lux"),
    LINEAR_ACCELERATION(Sensor.TYPE_LINEAR_ACCELERATION, "Linear Acceleration", SensorDataType.VECTOR3, "m/s²"),
    MAGNETIC_FIELD(Sensor.TYPE_MAGNETIC_FIELD, "Magnetic Field", SensorDataType.VECTOR3, "µT"),


}