package com.techyourchance.androidviews.demonstrations._07_path_shape

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.techyourchance.androidviews.CustomViewScaffold
import com.techyourchance.androidviews.R
import timber.log.Timber
import kotlin.math.sqrt

class PathShapeView : CustomViewScaffold {

    private val trianglePaint = Paint()
    private var triangleLineSize = 0f
    private val trianglePath = Path()

    private val viewBorderPaint = Paint()
    private val viewBorderRect = RectF()

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

        val viewBorderLineSize = dpToPx(VIEW_BORDER_LINE_SIZE_DP)
        val viewBorderPadding = viewBorderLineSize / 2
        // viewBorderPadding을 주는 이유는 left, top을 0f로 주면 너비가 0f를 기준으로 그려지기 때문에 왼쪽(left)과 위쪽(top)이 짤린다.
        viewBorderRect.set(
            viewBorderPadding,
            viewBorderPadding,
            width.toFloat() - viewBorderPadding,
            height.toFloat() - viewBorderPadding
        )

        viewBorderPaint.color = Color.RED
        viewBorderPaint.style = Paint.Style.STROKE
        viewBorderPaint.strokeWidth = viewBorderLineSize
        viewBorderPaint.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f) // 점선

        triangleLineSize = dpToPx(LINE_SIZE_DP)

        trianglePaint.color = ContextCompat.getColor(context, R.color.primary_variant)
        trianglePaint.style = Paint.Style.STROKE
        trianglePaint.strokeWidth = triangleLineSize
        trianglePaint.isAntiAlias = true // 가장자리 매끄럽게 true
        trianglePaint.strokeJoin = Paint.Join.ROUND

        updateTrianglePath(w, h, triangleLineSize)
    }

    private fun updateTrianglePath(width: Int, height: Int, minPadding: Float) {
        val triangleSideLength = computeMaxAvailableTriangleSideLength(width, height, minPadding)
        val triangleHorizontalMargin = (width - triangleSideLength) / 2
        Timber.d("MYTAG ${triangleSideLength} / ${triangleHorizontalMargin}")

        val triangleLeft = PointF(triangleHorizontalMargin, height.toFloat() - minPadding)
        val triangleRight = PointF(width - triangleHorizontalMargin, triangleLeft.y)
        val triangleTop = PointF(width / 2f, height - triangleSideLength * sqrt(3f) / 2)

        trianglePath.reset()
        trianglePath.moveTo(triangleTop.x, triangleTop.y) // 그리기 위치 시작점
        trianglePath.lineTo(triangleRight.x, triangleRight.y) // 오른쪽 지점까지 그리기
        trianglePath.lineTo(triangleLeft.x, triangleLeft.y) // 왼쪽 지점까지 그리기
        trianglePath.lineTo(triangleTop.x, triangleTop.y) // 시작 위치까지 그리기
        trianglePath.close() // close를 해야 닫힌다

        // 이런식으로 가능
        trianglePath.apply {
//            reset()
//            moveTo(triangleTop.x, triangleTop.y)
//            lineTo(triangleRight.x, triangleRight.y)
//            moveTo(triangleRight.x, triangleRight.y)
//            lineTo(triangleLeft.x, triangleLeft.y)
//            moveTo(triangleLeft.x, triangleLeft.y)
//            lineTo(triangleTop.x, triangleTop.y)
        }
    }

    // 정삼각형의 변의 길이를 구한다.
    private fun computeMaxAvailableTriangleSideLength(viewWidth: Int, viewHeight: Int, minPadding: Float): Float {
        val viewWidthMinusPadding = viewWidth - 2 * minPadding
        val heightForViewWidthMinusPadding = viewWidthMinusPadding * sqrt(3f) / 2 // 정삼각형 높이 구하는 공식
        Timber.d("MYTAG ${viewWidthMinusPadding} ${heightForViewWidthMinusPadding}")
        return if (viewHeight >= heightForViewWidthMinusPadding) { // 정삼각형 높이가 디바이스 View 높이보다 작으면, 밑변 길이 리턴
            viewWidthMinusPadding
        } else {
            return (viewHeight - 2 * minPadding) * 2 / sqrt(3f) // 정삼각형 높이가 디바이스 View 높이보다 크다면,디바이스 View 높이를 기준으로 다시 계산 후 리턴
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(trianglePath, trianglePaint)
        canvas.drawRect(viewBorderRect, viewBorderPaint)
    }

    companion object {
        const val LINE_SIZE_DP = 10f
        const val VIEW_BORDER_LINE_SIZE_DP = 1f
    }
}