package com.shiftpsh.sensortester.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class RatioView(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {
    var ratio = "9:16"
        set(value) {
            invalidate()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val calculatedRatio = ratio.split(":").map { it.toDouble() }.let { it[0] / it[1] }

        // setMeasuredDimension((measuredHeight * calculatedRatio).toInt(), measuredHeight)
        super.onMeasure(
                MeasureSpec.makeMeasureSpec((heightMeasureSpec * calculatedRatio).toInt(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(heightMeasureSpec, MeasureSpec.EXACTLY))
    }
}