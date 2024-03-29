package com.techyourchance.androidviews.exercises._01_

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.techyourchance.androidviews.CustomViewScaffold
import com.techyourchance.androidviews.R
import com.techyourchance.androidviews.exercises._03_.SliderChangeListener
import timber.log.Timber
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class MySliderView : CustomViewScaffold {

    private var sliderChangeListener: SliderChangeListener? = null

    private var linePaint = Paint()
    private var startX = 0f
    private var startY = 0f
    private var stopX = 0f
    private var stopY = 0f

    private var circlePaint = Paint()
    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f

    private var isDragged = false
    private var lastXPoint = 0f
    private var lastYPoint = 0f

    private var lineWidth = 0f
    private var currentPercentValue = 0.5f

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    fun setSliderChangeListener(listener: SliderChangeListener) {
        sliderChangeListener = listener
    }
    fun getCurrentPercentValue(): Float = currentPercentValue

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event == null) {
            return super.onTouchEvent(event)
        }

        // 뭐가 필요할까..
        // 원의 중심(x,y), 마지막으로 찍힌 좌표(x,y), 두 점 사이의 거리, 원의 반지름, 드래그 플래그, 현재 액션값
        val distanceBetweenXAndY = distanceBetweenXAndY(event.x, centerX, event.y, centerY)
        if(distanceBetweenXAndY <= radius && event.action == MotionEvent.ACTION_DOWN) {
            Timber.i("Start")
            isDragged = true
            lastXPoint = event.x
//            lastYPoint = event.y
            return true
        } else if(isDragged && event.action == MotionEvent.ACTION_MOVE) {
            val distanceX = event.x - lastXPoint
//            val distanceY = event.y - lastYPoint
            Timber.i("${centerX} ${startX} ${stopX}")
            if(event.x < startX) {
                Timber.i("Block1")
                centerX = startX
            } else if (event.x > stopX) {
                Timber.i("Block2")
                centerX = stopX
            } else if(distanceX < 0) {
                Timber.i("Set1 ${distanceX} / ${centerX + distanceX} / ${startX}")
                centerX = max(centerX + distanceX, startX)
            } else {
                Timber.i("Set2 ${distanceX} / ${centerX + distanceX} / ${stopX}")
                centerX = min(centerX + distanceX, stopX)
            }

//            centerX += distanceX
//            centerY += distanceY

            lastXPoint = event.x
//            lastYPoint = event.y

            updatePercentValue()
            invalidate()
            return true
        } else {
            Timber.i("Finish")
            isDragged = false
            return false
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        radius = h / 3f

        startX = radius
        startY = h / 2f
        stopX = w - radius
        stopY = h / 2f

        centerX = (stopX - startX) * currentPercentValue + startX // 비율로 원의 중심 설정, center를 맞추려면 회색 선 길이가 왼쪽 끝부터 startX만큼 띄워져 있기 때문에 더해줘야 한다..
        centerY = h / 2f

        Timber.i("MYTAG ${centerX} ${startX} ${stopX} ${w}")

        updatePercentValue()
    }

    private fun updatePercentValue() {
        lineWidth = stopX - startX
        val currentX = centerX - radius
        currentPercentValue = (currentX / lineWidth) * 1.0f
        Timber.i("MYTAG lineWidth: ${lineWidth} currentX: ${currentX} currentPercentValue: ${currentPercentValue}")
        sliderChangeListener?.onValueChanged(currentPercentValue)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        linePaint.strokeWidth = dpToPx(LINE_HEIGHT)
        linePaint.color = ContextCompat.getColor(context, R.color.gray_light)

        canvas.drawLine(startX, startY, stopX, stopY, linePaint)
        Timber.i("MYTAG ${startX} ${stopX}")

        circlePaint.style = Paint.Style.FILL
        circlePaint.color = ContextCompat.getColor(context, R.color.primary)
        circlePaint.alpha = 50
        canvas.drawCircle(centerX, centerY, radius, circlePaint)
    }

    private fun distanceBetweenXAndY(x1: Float, x2: Float, y1: Float, y2: Float): Float {
        val x = (x1 - x2) * (x1 - x2)
        val y = (y1 - y2) * (y1 - y2)
        return sqrt(x + y)
    }

    override fun onSaveInstanceState(): Parcelable? {
        return CircleSavedState(super.onSaveInstanceState(), currentPercentValue)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if(state is CircleSavedState) {
            super.onRestoreInstanceState(state.superSavedState)
            this.currentPercentValue = state.circleXCenterFraction
            invalidate()
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    companion object {
        const val LINE_HEIGHT = 10f
    }

    private class CircleSavedState: BaseSavedState {

        val superSavedState: Parcelable?
        val circleXCenterFraction: Float
//        val circleYCenterFraction: Float

        constructor(
            superSavedState: Parcelable?,
            circleXCenterFraction: Float,
//            circleYCenterFraction: Float,
        ): super(superSavedState) {
            this.superSavedState = superSavedState
            this.circleXCenterFraction = circleXCenterFraction
//            this.circleYCenterFraction = circleYCenterFraction
        }

        constructor(parcel: Parcel) : super(parcel) {
            this.superSavedState = parcel.readParcelable(null)
            this.circleXCenterFraction = parcel.readFloat()
//            this.circleYCenterFraction = parcel.readFloat()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeParcelable(superSavedState, flags)
            out.writeFloat(circleXCenterFraction)
//            out.writeFloat(circleYCenterFraction)
        }

        companion object CREATOR : Parcelable.Creator<CircleSavedState> {
            override fun createFromParcel(parcel: Parcel): CircleSavedState {
                return CircleSavedState(parcel)
            }

            override fun newArray(size: Int): Array<CircleSavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}