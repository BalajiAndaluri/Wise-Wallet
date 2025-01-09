package com.example.wisewallet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2

class NavigationActivity : AppCompatActivity() {
    private lateinit var slideViewPager: ViewPager2
    private lateinit var dotIndicator: LinearLayout
    private lateinit var backButton: Button
    private lateinit var nextButton: Button
    private lateinit var skipButton: Button
    private lateinit var dots: Array<TextView>
    private val viewPagerListener = object : ViewPager2.OnPageChangeCallback() {

        override fun onPageSelected(position: Int) {
            setDotIndicator(position)
            backButton.visibility = if (position > 0) View.VISIBLE else View.INVISIBLE
            nextButton.text = if (position == 2) "Finish" else "Next"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_navigation)
        backButton = findViewById(R.id.backButton)
        nextButton = findViewById(R.id.nextButton)
        skipButton = findViewById(R.id.skipButton)

        backButton.setOnClickListener {
            if (getItem(0) > 0) {
                slideViewPager.setCurrentItem(getItem(-1), true)
            }
        }

        nextButton.setOnClickListener{
            if (getItem(0) < 2) {
                slideViewPager.setCurrentItem(getItem(1), true)
            } else {
                val intent = Intent(this@NavigationActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        skipButton.setOnClickListener {
            val intent = Intent(this@NavigationActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(this.findViewById(R.id.Navigation)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        slideViewPager=findViewById(R.id.slideViewPager)
        dotIndicator=findViewById(R.id.dotIndicator)
        val viewPagerAdapter=ViewPagerAdapter(this)
        slideViewPager.adapter=viewPagerAdapter
        setDotIndicator(0)

    }
    fun setDotIndicator(position:Int){
        dots = Array(3) { TextView(this) }
        dotIndicator.removeAllViews()

        for (i in 0 until dots.size) {
            dots[i] = TextView(this)
            dots[i].text = "•"
            dots[i].textSize = 35f
            dots[i].setTextColor(getResources().getColor(R.color.grey))
            dotIndicator.addView(dots[i])
        }
        dots[position].setTextColor(getResources().getColor(R.color.pink))
    }

    private fun getItem(i: Int): Int {
        return slideViewPager.currentItem + i
    }
}