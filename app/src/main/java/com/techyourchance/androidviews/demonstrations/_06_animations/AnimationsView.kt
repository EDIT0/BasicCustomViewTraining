package com.techyourchance.androidviews.demonstrations._06_animations

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.techyourchance.androidviews.CustomViewScaffold
import com.techyourchance.androidviews.R
import timber.log.Timber
import kotlin.math.min

@SuppressLint("ClickableViewAccessibility")
class AnimationsView : CustomViewScaffold {

    private val paint = Paint()

    private var circleXCenter = 0f
    private var circleXMin = 0f
    private var circleXMax = 0f
    private var circleYCenter = 0f
    private var circleRadius = 0f

    private var borderSize = 0f
    private val borderRect = RectF()

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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        borderSize = dpToPx(BORDER_STROKE_SIZE_DP)
        borderRect.set(borderSize, borderSize, w - borderSize, height - borderSize)

        circleRadius = min(w.toFloat(), h.toFloat()) / 6f
        circleXCenter = w / 2f
        circleXMin = borderSize + circleRadius
        circleXMax = w - circleRadius - borderSize
        circleYCenter = h / 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = ContextCompat.getColor(context, R.color.primary_light)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderSize
        canvas.drawRect(borderRect, paint)
        paint.color = ContextCompat.getColor(context, R.color.primary_variant)
        paint.style = Paint.Style.FILL
        canvas.drawCircle(circleXCenter, circleYCenter, circleRadius, paint)
    }

    fun startAnimation(period: Long) {
        val currentOffsetFromMin = circleXCenter - circleXMin
        val fullRange = circleXMax - circleXMin
        valueAnimator = ValueAnimator.ofFloat(1.0f, 0f) // 범위 지정. 보통 0.0 ~ 1.0 사이
            .apply {
                interpolator = LinearInterpolator() // 다양한 interpolator -> https://m.blog.naver.com/tkddlf4209/220700530627
                duration = period / 2
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                setCurrentFraction(currentOffsetFromMin / fullRange)
                addUpdateListener {
                    val fraction = it.animatedValue as Float // fraction 값은 0 ~ 1 사이를 왔다갔다 한다.
                    circleXCenter = fraction * (circleXMax - circleXMin) + circleXMin
                    // circleXMax - circleXMin을 계산해서 움직일 범위를 구하고 +circleXMin을 통해 중앙으로 맞춰준다.
                    invalidate()
            }
            start()
        }
    }

    fun stopAnimation() {
        valueAnimator?.cancel()
    }

    companion object {
        private const val BORDER_STROKE_SIZE_DP = 2f
    }

}