package com.techyourchance.androidviews.demonstrations._04_drag

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techyourchance.androidviews.R
import com.techyourchance.androidviews.general.BaseFragment
import timber.log.Timber


class DragFragment : BaseFragment() {

    override val screenName get() = getString(R.string.screen_name_drag)
    
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return DragCircleView(context).apply {
            setOnTouchListener { view, motionEvent ->
                Timber.i("touch event with action ${motionEvent.action} ${motionEvent.x} ${motionEvent.y}")
                false // true면 DragCircleView로 이벤트가 전달되지 않는다.
            }
        }
    }

    companion object {
        fun newInstance(): DragFragment {
            return DragFragment()
        }
    }

}