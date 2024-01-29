package com.techyourchance.androidviews.exercises._01_

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import androidx.core.content.ContextCompat
import com.techyourchance.androidviews.CustomViewScaffold
import com.techyourchance.androidviews.R

class MySliderView : CustomViewScaffold {

    private var linePaint = Paint()
    private var startX = 0f
    private var startY = 0f
    private var stopX = 0f
    private var stopY = 0f

    private var circlePaint = Paint()
    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        startX = dpToPx(MARGIN)
        startY = h / 2f
        stopX = w - dpToPx(MARGIN)
        stopY = h / 2f

        centerX = w / 2f
        centerY = h / 2f
        radius = h / 3f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        linePaint.strokeWidth = dpToPx(LINE_HEIGHT)
        linePaint.color = ContextCompat.getColor(context, R.color.gray_light)

        canvas.drawLine(startX, startY, stopX, stopY, linePaint)

        circlePaint.style = Paint.Style.FILL
        circlePaint.color = ContextCompat.getColor(context, R.color.primary)
        canvas.drawCircle(centerX, centerY, radius, circlePaint)
    }

    companion object {
        const val MARGIN = 20f
        const val LINE_HEIGHT = 10f
    }
}