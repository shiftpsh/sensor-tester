package com.shiftpsh.sensortester.extension

fun List<String>?.examples(): String {
    return when {
        this == null -> ""
        size > 0 -> take(Math.min(5, size)).joinToString(", ")
        else -> ""
    }
}

fun List<Any>?.formatSize(singular: String, plural: String): String {
    return if (this == null) "0 $plural"
    else size.formatNumber(singular, plural)
}