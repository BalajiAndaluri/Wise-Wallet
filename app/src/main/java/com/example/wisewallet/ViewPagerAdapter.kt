package com.example.wisewallet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter

class ViewPagerAdapter(var context: Context) : PagerAdapter() {

    var sliderAllImages: IntArray = intArrayOf(
        R.drawable.trade_mark,
        R.drawable.auth,
        R.drawable.bank_message,
        R.drawable.manual,
        R.drawable.piechart // Add image for the 5th screen
    )

    var sliderAllTitle: IntArray = intArrayOf(
        R.string.c1,
        R.string.c2,
        R.string.c3,
        R.string.c4,
        R.string.c5  // Add title for the 5th screen
    )

    var sliderAllDesc: IntArray = intArrayOf(
        R.string.m1,
        R.string.m2,
        R.string.m3,
        R.string.m4,
        R.string.m5  // Add description for the 5th screen
    )

    override fun getCount(): Int {
        return sliderAllTitle.size // Now returns 5
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.onboarding_slider, container, false)

        val sliderImage = view.findViewById<View>(R.id.sliderImage) as ImageView
        val sliderTitle = view.findViewById<View>(R.id.sliderTitle) as TextView
        val sliderDesc = view.findViewById<View>(R.id.sliderDesc) as TextView

        sliderImage.setImageResource(sliderAllImages[position])
        sliderTitle.setText(sliderAllTitle[position])
        sliderDesc.setText(sliderAllDesc[position])

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}