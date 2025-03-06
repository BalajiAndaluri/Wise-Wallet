package com.example.wisewallet
import com.example.wisewallet.R
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager


class NavigationActivity : AppCompatActivity() {
    private var slideViewPager: ViewPager? = null
    lateinit private var dotIndicator: LinearLayout
    lateinit private var backButton: Button
    lateinit private var nextButton: Button
    lateinit private var skipButton: Button
    lateinit var dots: Array<TextView?>
    private lateinit var viewPagerAdapter: ViewPagerAdapter


    var viewPagerListener: ViewPager.OnPageChangeListener = object :
        ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            setDotIndicator(position)

            if (position > 0) {
                backButton.visibility = View.VISIBLE
            } else {
                backButton.visibility = View.INVISIBLE
            }
            if (position == 4) {
                nextButton.text = "Finish"
                nextButton.setOnClickListener{
                    val i = Intent(
                        this@NavigationActivity,
                        LoginPhoneNumberActivity::class.java
                    )
                    startActivity(i)
                    finish()
                }
            } else {
                nextButton.text = "Next"
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding_navigation)

        backButton = findViewById(R.id.backButton)
        nextButton = findViewById(R.id.nextButton)
        skipButton = findViewById(R.id.skipButton)

        backButton.setOnClickListener( {
            if (getItem(0) > 0) {
                slideViewPager!!.setCurrentItem(getItem(-1), true)
            }
        })

        nextButton.setOnClickListener( {
            if (getItem(0) < 5) slideViewPager!!.setCurrentItem(getItem(1), true)
            else {
                val i = Intent(
                    this@NavigationActivity,
                    GetStarted::class.java
                )
                startActivity(i)
                finish()
            }
        })

        skipButton.setOnClickListener( {
            val i = Intent(
                this@NavigationActivity,
                LoginPhoneNumberActivity::class.java
            )
            startActivity(i)
            finish()
        })

        slideViewPager = findViewById<View>(R.id.slideViewPager) as ViewPager
        dotIndicator = findViewById<View>(R.id.dotIndicator) as LinearLayout

        viewPagerAdapter = ViewPagerAdapter(this)
        slideViewPager!!.adapter = viewPagerAdapter

        setDotIndicator(0)
        slideViewPager!!.addOnPageChangeListener(viewPagerListener)
    }

    fun setDotIndicator(position: Int) {
        dots = arrayOfNulls(5)
        dotIndicator.removeAllViews()

        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]!!.text = "\u2022"
            dots[i]!!.textSize = 35f
            dots[i]!!.setTextColor(resources.getColor(R.color.grey, applicationContext.theme))
            dotIndicator.addView(dots[i])
        }
        dots[position]!!
            .setTextColor(resources.getColor(R.color.matt_green, applicationContext.theme))
    }

    private fun getItem(i: Int): Int {
        return slideViewPager!!.currentItem + i
    }
}