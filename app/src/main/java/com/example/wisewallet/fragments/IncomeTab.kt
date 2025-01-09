package com.example.wisewallet.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TableLayout
import android.widget.Toast
import com.example.wisewallet.R


class IncomeTab : Fragment() {

    private lateinit var imageButton: ImageButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_income_tab, container, false)
        // Inflate the layout for this fragment
        val myFrameLayout00: FrameLayout = view.findViewById(R.id.IIcon00)
        myFrameLayout00.setOnClickListener {
            // Handle the click event here
            // For example:
            Toast.makeText(requireContext(), "Salary clicked!", Toast.LENGTH_SHORT).show()
        }
        val myFrameLayout01: FrameLayout = view.findViewById(R.id.IIcon01)
        myFrameLayout01.setOnClickListener {
            // Handle the click event here
            // For example:
            Toast.makeText(requireContext(), "Home1 Rent clicked!", Toast.LENGTH_SHORT).show()
        }
        val myFrameLayout02: FrameLayout = view.findViewById(R.id.IIcon02)
        myFrameLayout02.setOnClickListener {
            // Handle the click event here
            // For example:
            Toast.makeText(requireContext(), "Home2 Rent clicked!", Toast.LENGTH_SHORT).show()
        }
        val myFrameLayout03: FrameLayout = view.findViewById(R.id.IIcon03)
        myFrameLayout03.setOnClickListener {
            // Handle the click event here
            // For example:
            Toast.makeText(requireContext(), "Settings clicked!", Toast.LENGTH_SHORT).show()
        }
        return view
    }
}