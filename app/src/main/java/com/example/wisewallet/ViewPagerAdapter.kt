package com.example.wisewallet

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import android.view.LayoutInflater

class ViewPagerAdapter(private val context: Context) : PagerAdapter() {


    private val sliderAllCaption = intArrayOf(
        R.string.c1,
        R.string.c2,
        R.string.c3,
        R.string.c4
    )

    private val sliderAllDesc = intArrayOf(
        R.string.m1,
        R.string.m2,
        R.string.m3,
        R.string.m4
    )

    override fun getCount(): Int = sliderAllCaption.size

    override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj as LinearLayout

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.slider_screen, container, false)
        val sliderCaption = view.findViewById<TextView>(R.id.Caption)
        val sliderDesc = view.findViewById<TextView>(R.id.SliderDesc)

        sliderCaption.setText(sliderAllCaption[position])
        sliderDesc.setText(sliderAllDesc[position])

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as LinearLayout)
    }
}