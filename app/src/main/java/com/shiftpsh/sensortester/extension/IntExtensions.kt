package com.shiftpsh.sensortester.extension

fun Int.formatNumber(singular: String, plural: String): String {
    return if (this == 1) {
        "$this $singular"
    } else {
        "$this $plural"
    }
}

fun String.makeFloat() = replace(Regex("[^\\d.]"), "").toFloat()