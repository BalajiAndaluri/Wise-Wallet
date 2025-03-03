package com.example.wisewallet.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TableLayout
import android.widget.Toast
import com.example.wisewallet.ManualEntryActivity
import com.example.wisewallet.ManualEntryActivityIncome
import com.example.wisewallet.R


class IncomeTab : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_income_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners(view)
    }

    private fun setupClickListeners(view: View) {
        val imageButtonIds = arrayOf(
            R.id.IIcon00, R.id.IIcon01, R.id.IIcon02,
            R.id.IIcon10, R.id.IIcon11, R.id.IIcon12
        )

        for (imageButtonId in imageButtonIds) {
            val imageButton: ImageButton? = view.findViewById(imageButtonId)
            imageButton?.setOnClickListener {
                if (isAdded && context != null) {
                    val message = getToastMessage(imageButtonId)
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    //Log.d("ExpenseTab", "Clicked ImageButton: $imageButtonId, Message: $message")
                } else {
                    Log.e("ExpenseTab", "Context or Fragment not available")
                }
                openManualEntryPage(imageButtonId)
            }
        }
    }

    private fun openManualEntryPage(imageButtonId: Int) {
        val intent = Intent(requireContext(), ManualEntryActivityIncome::class.java)
        intent.putExtra("imageButtonId", imageButtonId)
        startActivity(intent)
    }

    private fun getToastMessage(imageButtonId: Int): String {
        return when (imageButtonId) {
            R.id.IIcon00 -> "Salary clicked!"
            R.id.IIcon01 -> "Bonus clicked!"
            R.id.IIcon02 -> "Rent clicked!"
            R.id.IIcon10 -> "Interest clicked!"
            R.id.IIcon11 -> "Others clicked!"
            R.id.IIcon12 -> "Rupee clicked!"

            else -> "Clicked!"
        }
    }
}