package com.techyourchance.androidviews.demonstrations._03_basic_touch

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.techyourchance.androidviews.R
import com.techyourchance.androidviews.general.BaseFragment
import timber.log.Timber


class BasicTouchFragment : BaseFragment() {

    override val screenName get() = getString(R.string.screen_name_basic_touch)

    private lateinit var view: View
    
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        view = View(context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.setOnTouchListener { _, event ->
            Timber.i("touch event ${getEventAction(event)} at (${event.x}, ${event.y});" +
                    " raw coordinates (${event.rawX}, ${event.rawY})")
            true
        }
    }

    private fun getEventAction(event: MotionEvent): String {
        return when(event.action) {
            0 -> "ACTION_DOWN"
            1 -> "ACTION_UP"
            2 -> "ACTION_MOVE"
            3 -> "ACTION_CANCEL"
            else -> "ACTION_ID:${event.action}"
        }
    }

    companion object {
        fun newInstance(): BasicTouchFragment {
            return BasicTouchFragment()
        }
    }

}