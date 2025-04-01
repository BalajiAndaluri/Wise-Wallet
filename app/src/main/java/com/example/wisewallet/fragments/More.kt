package com.example.wisewallet.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.wisewallet.R

class More : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_more, container, false)
        val question1 = view.findViewById<TextView>(R.id.question1)
        val answer1 = view.findViewById<TextView>(R.id.answer1)
        val question2 = view.findViewById<TextView>(R.id.question2)
        val answer2 = view.findViewById<TextView>(R.id.answer2)
        val question3 = view.findViewById<TextView>(R.id.question3)
        val answer3 = view.findViewById<TextView>(R.id.answer3)
        if (question1 != null) {
            question1.setOnClickListener {
                if (answer1 != null) {
                    answer1.visibility = if (answer1.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                }
                if (answer1 != null) {
                    toggleArrow(question1, answer1.visibility)
                }
            }
        }

        if (question2 != null) {
            question2.setOnClickListener {
                if (answer2 != null) {
                    answer2.visibility = if (answer2.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                }
                if (answer2 != null) {
                    toggleArrow(question2, answer2.visibility)
                }
            }
        }

        if (question3 != null) {
            question3.setOnClickListener {
                if (answer3 != null) {
                    answer3.visibility = if (answer3.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                }
                if (answer3 != null) {
                    toggleArrow(question3, answer3.visibility)
                }
            }
        }


        return view
    }

    private fun toggleArrow(textView: TextView, visibility: Int) {
        val drawableEnd = if (visibility == View.VISIBLE) {
            resources.getDrawable(R.drawable.baseline_arrow_drop_up_24, null) // Replace with your up-arrow drawable
        } else {
            resources.getDrawable(R.drawable.baseline_arrow_drop_down_24, null) // Replace with your down-arrow drawable
        }
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawableEnd, null)

    }


}