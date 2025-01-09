package com.example.wisewallet.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.wisewallet.R
import com.example.wisewallet.R.id.direct_to_setting

class ExpenseTab : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_expense_tab, container, false)
        // Inflate the layout for this fragment
        val myFrameLayout00: FrameLayout = view.findViewById(R.id.EIcon00)
        myFrameLayout00.setOnClickListener {
            // Handle the click event here
            // For example:
            val toast=Toast.makeText(requireContext(), "Salary clicked!", Toast.LENGTH_SHORT)
            toast.show()
        }
        val myFrameLayout01: FrameLayout = view.findViewById(R.id.EIcon01)
        myFrameLayout01.setOnClickListener {
            // Handle the click event here
            // For example:
            Toast.makeText(requireContext(), "Salary clicked!", Toast.LENGTH_SHORT).show()
        }
        val myFrameLayout02: FrameLayout = view.findViewById(R.id.EIcon02)
        myFrameLayout02.setOnClickListener {
            // Handle the click event here
            // For example:
            Toast.makeText(requireContext(), "Salary clicked!", Toast.LENGTH_SHORT).show()
        }
        val myFrameLayout03: FrameLayout = view.findViewById(R.id.EIcon03)
        myFrameLayout03.setOnClickListener {
            // Handle the click event here
            // For example:
            Toast.makeText(requireContext(), "Salary clicked!", Toast.LENGTH_SHORT).show()
        }
        val myFrameLayout10: FrameLayout = view.findViewById(R.id.EIcon10)
        myFrameLayout10.setOnClickListener {
            // Handle the click event here
            // For example:
            Toast.makeText(requireContext(), "Salary clicked!", Toast.LENGTH_SHORT).show()
        }
        val myFrameLayout11: FrameLayout = view.findViewById(R.id.EIcon11)
        myFrameLayout11.setOnClickListener {
            // Handle the click event here
            // For example:
            Toast.makeText(requireContext(), "Salary clicked!", Toast.LENGTH_SHORT).show()
        }
        val myFrameLayout12: FrameLayout = view.findViewById(R.id.EIcon12)
        myFrameLayout12.setOnClickListener {
            // Handle the click event here
            // For example:
            Toast.makeText(requireContext(), "Salary clicked!", Toast.LENGTH_SHORT).show()
        }
        val myFrameLayout13: FrameLayout = view.findViewById(R.id.EIcon13)
        myFrameLayout13.setOnClickListener {
            // Handle the click event here
            // For example:
            Toast.makeText(requireContext(), "Salary clicked!", Toast.LENGTH_SHORT).show()
        }

        return view
    }

}