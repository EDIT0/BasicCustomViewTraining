package com.techyourchance.androidviews.exercises._05_

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.techyourchance.androidviews.CustomViewScaffold
import com.techyourchance.androidviews.R
import timber.log.Timber
import kotlin.math.pow
import kotlin.math.sqrt

class MyCheckmarkView : CustomViewScaffold {

    private val checkPaint = Paint() // 그리기 설정
    private var checkLineSize = 0f // 두께
    private val checkPath = Path() // 경로 그리기
    private val checkPathForAnimation = Path() // 애니메이션을 위한 경로

    private var shortSideLength = 0f

    private var valueAnimator: ValueAnimator? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    fun startAnimation(durationMs: Long) {
        valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            interpolator = LinearInterpolator()
            duration = durationMs
            addUpdateListener {
                updateCheckPath(it.animatedValue as Float)
            }
            start()
        }
    }

    fun stopAnimation() {
        valueAnimator?.cancel()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        checkPaint.apply {
            color = ContextCompat.getColor(context, R.color.green)
            style = Paint.Style.STROKE
            strokeWidth = dpToPx(LINE_SIZE_DP)
        }

//        width = sqrt(a^2 + (2a)^2)
//        height = a * 2a / sqrt(a^2 + (2a)^2) = 2a / sqrt(5)

        checkLineSize = dpToPx(LINE_SIZE_DP)

        shortSideLength = calculateCheckmarkShortSideLength(w, h, checkLineSize) // 짧은 변 길이

//        val checkWidth = sqrt(5f) * shortSideLength
        val checkWidth = sqrt(shortSideLength.pow(2) + (2 * shortSideLength).pow(2)) // 피타고라스 빗변 길이
        val checkHeight = 2 * shortSideLength / sqrt(5f) // 높이 구하기
        Timber.d("MYTAG checkWidth: ${checkWidth} checkHeight: ${checkHeight} ${sqrt(shortSideLength.pow(2) - checkHeight.pow(2))}")

        val startPoint = PointF((w - checkWidth) / 2, (h - checkHeight) / 2)
        // middlePoint의 x: 기존 x 시작점 + 피타고라스 공식 이용(대각선^2 - 높이^2 = 밑변)
        val middlePoint = PointF(((w - checkWidth) / 2) + sqrt(shortSideLength.pow(2) - checkHeight.pow(2)), h - ((h - checkHeight) / 2))
        val endPoint = PointF(w - ((w - checkWidth) / 2), (h - checkHeight) / 2)

        checkPath.apply {
            reset()
            moveTo(startPoint.x, startPoint.y)
            lineTo(middlePoint.x, middlePoint.y)
            lineTo(endPoint.x, endPoint.y)
        }
    }

    private fun calculateCheckmarkShortSideLength(viewWidth: Int, viewHeight: Int, minPadding: Float): Float {
        Timber.d("MYTAG ${viewWidth} ${viewHeight} ${minPadding}")
        val checkmarkShortSideLengthCandidate = sqrt((viewWidth - 2 * minPadding).pow(2) / 5)
        val checkMarkHeightCandidate = 2 * checkmarkShortSideLengthCandidate / sqrt(5f)
        Timber.d("MYTAG ${checkMarkHeightCandidate} ${checkMarkHeightCandidate}")
        return if (checkMarkHeightCandidate <= viewHeight - 2 * minPadding) {
            checkmarkShortSideLengthCandidate
        } else {
            (viewHeight - 2 * minPadding) * sqrt(5f) / 2
        }
    }

    private fun updateCheckPath(fraction: Float) { // fraction 0 ~ 1 => 애니메이션 한 사이클 경로라고 보면 됨 (0 ~ 1)
        val pathMeasure = PathMeasure(checkPath, false) // 경로 측정, 완성된 삼각형 Path
        val totalLength = shortSideLength * 3
        checkPathForAnimation.reset()

        if(fraction <= 1f) {
            pathMeasure.getSegment(0f, fraction * totalLength, checkPathForAnimation, true)
        } else {

        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(checkPathForAnimation, checkPaint)
    }

    companion object {
        const val LINE_SIZE_DP = 15f
    }
}