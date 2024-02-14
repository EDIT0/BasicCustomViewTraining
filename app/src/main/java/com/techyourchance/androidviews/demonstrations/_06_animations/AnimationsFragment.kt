package com.techyourchance.androidviews.demonstrations._06_animations

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Choreographer
import android.view.Choreographer.FrameCallback
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import com.techyourchance.androidviews.R
import com.techyourchance.androidviews.general.BaseFragment
import kotlin.math.pow


class AnimationsFragment : BaseFragment() {

    override val screenName get() = getString(R.string.screen_name_animations)

    private val loopAnimator = LoopAnimator()
    private var objectAnimator: ObjectAnimator? = null

    private lateinit var viewAnimations: AnimationsView
    private lateinit var viewAnimations2: AnimationsView
    private lateinit var viewAnimations3: AnimationsView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return layoutInflater.inflate(R.layout.layout_animations, container, false).apply {
            viewAnimations = findViewById(R.id.viewAnimations)
            viewAnimations2 = findViewById(R.id.viewAnimations2)
            viewAnimations3 = findViewById(R.id.viewAnimations3)
            loopAnimator.listener = object : LoopAnimatorListener {
                override fun onAnimatedValueChanged(value: Float) {
                    viewAnimations.translationX = value
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()). post {
            val animationAmplitude = viewAnimations.width * ANIMATION_AMPLITUDE_TO_WIDTH_FRACTION
            loopAnimator.startAnimation(
                ANIMATION_PERIOD_NS,
                animationAmplitude,
                -animationAmplitude,
                viewAnimations.translationX
            )
            objectAnimator = ObjectAnimator.ofFloat(
                viewAnimations2, // Target Object
                "translationX",
                animationAmplitude, // 진폭
                -animationAmplitude // 진폭
            ).apply {
                interpolator = LinearInterpolator() // 일정하게
                duration = ANIMATION_PERIOD_NS / 1_000_000 / 2 // 하나의 애니메이션에 대한 지속시간
                repeatCount = ValueAnimator.INFINITE // 반복 횟수
                repeatMode = ValueAnimator.REVERSE // 애니메이션이 끝난 후에 RESTART, REVERSE 어떻게 할 것인지?
                val relativePosition = (viewAnimations.translationX + animationAmplitude) / (2 * animationAmplitude)
                setCurrentFraction(relativePosition) // 애니메이션 시작 지점 설정 (0.0 ~ 1.0 사이)
                start()
            }

            viewAnimations3.startAnimation(ANIMATION_PERIOD_NS / 1_000_000)
        }
    }

    override fun onPause() {
        super.onPause()
        loopAnimator.stopAnimation()
        objectAnimator?.cancel()
        viewAnimations3.stopAnimation()
    }

    companion object {
        private val ANIMATION_PERIOD_NS = 2 * 10f.pow(9).toLong() // 2 seconds
        private const val ANIMATION_AMPLITUDE_TO_WIDTH_FRACTION = 0.5f

        fun newInstance(): AnimationsFragment {
            return AnimationsFragment()
        }
    }

    private inner class LoopAnimator {

        var listener: LoopAnimatorListener? = null

        private var animationPeriodNanos = 0L
        private var animatedValueMin = 0f
        private var animatedValueMax = 0f
        private var animatedValueStart = 0f
        private var startTimeNanos = 0L
        private var animatedValueStartFraction = 0f

        private val choreographer: Choreographer = Choreographer.getInstance()
        private val animationFrameCallback = object : FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                val animatedValue = calculateAnimatedValue(frameTimeNanos)
                listener?.onAnimatedValueChanged(animatedValue)
                if (this@AnimationsFragment.isResumed) {
                    // Run this callback again on the next frame
                    choreographer.postFrameCallback(this)
                }
            }
        }

        fun startAnimation(
            animationPeriodNanos: Long,
            animatedValueMin: Float,
            animatedValueMax: Float,
            animatedValueStart: Float,
        ) {
            this.animationPeriodNanos = animationPeriodNanos
            this.animatedValueMin = animatedValueMin
            this.animatedValueMax = animatedValueMax
            this.animatedValueStart = animatedValueStart
            startTimeNanos = System.nanoTime()
            animatedValueStartFraction = (animatedValueStart - animatedValueMin) / (animatedValueMax - animatedValueMin)
            choreographer.postFrameCallback(animationFrameCallback)
        }

        fun stopAnimation() {
            choreographer.removeFrameCallback(animationFrameCallback)
        }

        private fun calculateAnimatedValue(frameTimeNanos: Long): Float {
            // calculate elapsed time since the start of the animation
            val elapsedTimeNanos = frameTimeNanos - startTimeNanos

            // calculate the phase of the animation in the range of [0, 2)
            var phaseBase = (elapsedTimeNanos % animationPeriodNanos).toFloat() / animationPeriodNanos * 2

            // account for the initial phase shift
            phaseBase += animatedValueStartFraction
            phaseBase = if (phaseBase > 2) {
                4 - phaseBase
            } else if (phaseBase < 0) {
                0 - phaseBase
            } else {
                phaseBase
            }

            // flip the second half of the phase to create oscillation in the range [0, 1)
            val phase = if (phaseBase > 1.0)  {
                2.0f - phaseBase
            } else {
                phaseBase
            }

            // map the phase to the range of animatedValueMin to animatedValueMax
            return animatedValueMin + phase * (animatedValueMax - animatedValueMin)
        }

    }

    private interface LoopAnimatorListener {
        fun onAnimatedValueChanged(value: Float)
    }

}