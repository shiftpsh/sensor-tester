package com.shiftpsh.sensortester.extension

import android.content.Context
import android.graphics.Rect
import android.view.View

fun View.isCompletelyVisible(context: Context): Boolean {
    if (!isShown) {
        return false
    }

    val metrics = context.resources.displayMetrics
    val actualPosition = Rect()
    getGlobalVisibleRect(actualPosition)
    val screen = Rect(0, 0, metrics.widthPixels, metrics.heightPixels)
    return actualPosition.intersect(screen)
}