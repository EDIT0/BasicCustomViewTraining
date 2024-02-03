package com.techyourchance.androidviews.exercises._01_

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.techyourchance.androidviews.general.BaseFragment
import com.techyourchance.androidviews.R
import com.techyourchance.androidviews.exercises._03_.SliderChangeListener
import timber.log.Timber

class MySliderFragment : BaseFragment(), SliderChangeListener {

    override val screenName get() = getString(R.string.screen_name_my_slider)

    private lateinit var sliderView: MySliderView
    private lateinit var txtValue: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        return layoutInflater.inflate(R.layout.layout_my_slider, container, false).apply {
            sliderView = findViewById(R.id.sliderView)
            txtValue = findViewById(R.id.txtValue)

            sliderView.setSliderChangeListener(this@MySliderFragment)
        }
    }

    override fun onValueChanged(value: Float) {
        Timber.i("onValueChanged() value: ${value}")
        txtValue.text = value.toString()
    }

    override fun onResume() {
        super.onResume()
        txtValue.text = sliderView.getCurrentPercentValue().toString()
    }

    companion object {
        fun newInstance(): MySliderFragment {
            return MySliderFragment()
        }
    }
}