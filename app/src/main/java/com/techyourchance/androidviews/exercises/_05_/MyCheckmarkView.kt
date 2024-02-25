package com.techyourchance.androidviews.exercises._05_

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnStart
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
    private var checkScaleFractionForAnimation = 1f // 스케일 애니메이션을 위한 값
    private var checkRotateFractionForAnimation = 0f

    private var shortSideLength = 0f

    private var startPoint = PointF()
    private var middlePoint = PointF()
    private var endPoint = PointF()

    private var valueAnimator: ValueAnimator? = null
    private var scaleAnimator: ValueAnimator? = null
    private var animatorSet: AnimatorSet? = null

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
            doOnStart {
                ValueAnimator.ofFloat(0f, -10f, 10f, 0f).apply {
                    interpolator = LinearInterpolator()
                    duration = durationMs + durationMs / 2
                    addUpdateListener {
                        updateCheckRotate(it.animatedValue as Float)
                    }
                    start()
                }
            }
        }

        scaleAnimator = ValueAnimator.ofFloat(1f, 1.2f, 1f).apply {
            interpolator = LinearInterpolator()
            duration = durationMs
            addUpdateListener {
                updateCheckScale(it.animatedValue as Float)
            }
        }

        animatorSet = AnimatorSet().apply {
//            play(valueAnimator)
//            play(scaleAnimator)
            playSequentially(valueAnimator, scaleAnimator)
            start()
        }
    }

    fun stopAnimation() {
        valueAnimator?.cancel()
        animatorSet?.cancel()
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

        startPoint = PointF((w - checkWidth) / 2, (h - checkHeight) / 2)
        // middlePoint의 x: 기존 x 시작점 + 피타고라스 공식 이용(대각선^2 - 높이^2 = 밑변)
        middlePoint = PointF(((w - checkWidth) / 2) + sqrt(shortSideLength.pow(2) - checkHeight.pow(2)), h - ((h - checkHeight) / 2))
        endPoint = PointF(w - ((w - checkWidth) / 2), (h - checkHeight) / 2)

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

    private fun updateCheckPath(fraction: Float) { // fraction은 ofFloat()의 범위에 따라 값을 반환 ofFloat(0f, 1f) 0 ~ 1 => 애니메이션 한 사이클 경로라고 보면 됨 (0 ~ 1)
        val pathMeasure = PathMeasure(checkPath, false) // 경로 측정, 완성된 삼각형 Path
        val totalLength = shortSideLength * 3
        checkPathForAnimation.reset()

        Timber.d("MYTAG valueAnimator fraction: ${fraction}")

        if (fraction <= 1f) {
            pathMeasure.getSegment(0f, fraction * totalLength, checkPathForAnimation, true)
        } else {

        }
        invalidate()
    }

    private fun updateCheckScale(fraction: Float) {
        checkScaleFractionForAnimation = fraction
        Timber.d("MYTAG sacleAnimator fraction: ${fraction}")
        invalidate()
    }

    private fun updateCheckRotate(fraction: Float) {
        checkRotateFractionForAnimation = fraction
        Timber.d("MYTAG rotateAnimator fraction: ${fraction}")
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // scaleX, scaleY: 서로 x와 y방향으로 확대, 축소 된다.
        // middle.x, y 기준으로 확대, 축소
        // 1 ~ 1.2, 1.2 ~ 1
        canvas.scale(checkScaleFractionForAnimation, checkScaleFractionForAnimation, middlePoint.x, middlePoint.y)
        canvas.rotate(checkRotateFractionForAnimation, middlePoint.x, middlePoint.y)
        canvas.drawPath(checkPathForAnimation, checkPaint)
    }

    companion object {
        const val LINE_SIZE_DP = 15f
    }
}