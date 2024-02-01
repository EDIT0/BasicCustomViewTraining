package com.techyourchance.androidviews.demonstrations._04_drag

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.techyourchance.androidviews.CustomViewScaffold
import com.techyourchance.androidviews.R
import timber.log.Timber
import kotlin.math.min
import kotlin.math.sqrt

@SuppressLint("ClickableViewAccessibility")
class DragCircleView : CustomViewScaffold {

    private val paint = Paint()

    private var circleXCenter = 0f
    private var circleYCenter = 0f
    private var circleRadius = 0f

    private var lastMotionEventX = 0f
    private var lastMotionEventY = 0f
    private var isDragged = false

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return super.onTouchEvent(event)
        }
        val distanceFromCircleCenter = pointsDistance(event.x, event.y, circleXCenter, circleYCenter)
        if (distanceFromCircleCenter <= circleRadius && event.action == MotionEvent.ACTION_DOWN) {
            // 두 점 사이의 거리가 원의 반지름보다 작고, 액션이 Down = 0 이면?
            isDragged = true
            lastMotionEventX = event.x
            lastMotionEventY = event.y
            return true
        } else if (isDragged && event.action == MotionEvent.ACTION_MOVE) {
            // 드래그 중이고, 액션이 Move = 2 이면?

            // 원의 중심을 옮겨 주어야 하기 때문에 기존 원에서 새로운 원까지의 거리를 계산 (dx, dy)
            val dx = event.x - lastMotionEventX
            val dy = event.y - lastMotionEventY
            circleXCenter += dx
            circleYCenter += dy
            lastMotionEventX = event.x
            lastMotionEventY = event.y
            invalidate()
//            postInvalidate()
            return true
        } else {
            // 그 외 액션은 드래그 멈춤
            isDragged = false
            return false
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        circleXCenter = w / 2f
        circleYCenter = h / 2f
        circleRadius = min(w.toFloat(), h.toFloat()) / 6f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = ContextCompat.getColor(context, R.color.primary_variant)
        paint.style = Paint.Style.FILL
        canvas.drawCircle(circleXCenter, circleYCenter, circleRadius, paint)
    }

    /**
     * Compute the Euclidean distance between two points using the Pythagorean theorem
     * 두 점 사이의 거리
     */
    private fun pointsDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val dx = x1 - x2
        val dy = y1 - y2
        val dx2 = dx * dx
        val dy2 = dy * dy
        return sqrt(dx2 + dy2) // 루트
    }

}