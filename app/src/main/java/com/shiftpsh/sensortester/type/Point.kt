package com.shiftpsh.sensortester.type

class Point(var x: Float, var y: Float) {

    constructor(x: Number, y: Number) : this(x.toFloat(), y.toFloat())

    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
    operator fun plusAssign(other: Point) {
        x += other.x; y += other.y
    }

    operator fun unaryMinus() = Point(-x, -y)
    operator fun minus(other: Point) = this + (-other)
    operator fun minusAssign(other: Point) {
        this += -other
    }

    operator fun times(other: Float) = Point(x * other, y * other)
    operator fun times(other: Int) = this * other.toFloat()
    operator fun times(other: Point) = Point(x * other.x, y * other.y)
    operator fun timesAssign(other: Float) {
        x *= other; y *= other
    }

    operator fun timesAssign(other: Int) {
        x *= other.toFloat()
    }

    operator fun timesAssign(other: Point) {
        x *= other.x; y *= other.y
    }

    operator fun div(other: Float) = this * (1 / other)
    operator fun div(other: Int) = this / other.toFloat()
    operator fun div(other: Point) = this * (1 / other)
    operator fun divAssign(other: Float) {
        x /= other; y /= other
    }

    operator fun divAssign(other: Int) {
        x /= other.toFloat()
    }

    operator fun divAssign(other: Point) {
        x /= other.x; y /= other.y
    }

    override fun toString() = "(%.2f, %.2f)".format(x, y)

}

operator fun Float.times(other: Point) = other * this
operator fun Int.times(other: Point) = other * this

operator fun Float.div(other: Point) = Point(this / other.x, this / other.y)
operator fun Int.div(other: Point) = toFloat() / other