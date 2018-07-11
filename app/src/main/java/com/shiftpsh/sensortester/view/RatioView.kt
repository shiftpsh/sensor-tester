package com.shiftpsh.sensortester.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import timber.log.Timber

class RatioView(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {
    var ratio = "16:9"
        set(value) {
            field = value
            requestLayout()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val calculatedRatio = ratio.split(":").map { it.toDouble() }.let { it[1] / it[0] }
        Timber.d("ratio: $calculatedRatio")
        // setMeasuredDimension((measuredHeight * calculatedRatio).toInt(), measuredHeight)
        super.onMeasure(
                MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(heightMeasureSpec) * calculatedRatio).toInt(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY))
    }
}