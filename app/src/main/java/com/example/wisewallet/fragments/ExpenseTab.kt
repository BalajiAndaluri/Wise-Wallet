package com.example.wisewallet.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import com.example.wisewallet.ManualEntryActivity
import com.example.wisewallet.R

class ExpenseTab : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_expense_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners(view)
    }

    private fun setupClickListeners(view: View) {
        val imageButtonIds = arrayOf(
            R.id.EIcon00, R.id.EIcon01, R.id.EIcon02, R.id.EIcon03,
            R.id.EIcon10, R.id.EIcon11, R.id.EIcon12, R.id.EIcon13
        )

        for (imageButtonId in imageButtonIds) {
            val imageButton: ImageButton? = view.findViewById(imageButtonId)
            imageButton?.setOnClickListener {
                if (isAdded && context != null) {
                    val message = getToastMessage(imageButtonId)
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    Log.d("ExpenseTab", "Clicked ImageButton: $imageButtonId, Message: $message")
                } else {
                    Log.e("ExpenseTab", "Context or Fragment not available")
                }
                openManualEntryPage(imageButtonId)
            }
        }
    }

    private fun openManualEntryPage(imageButtonId: Int) {
        val intent = Intent(requireContext(), ManualEntryActivity::class.java)
        intent.putExtra("imageButtonId", imageButtonId)
        startActivity(intent)
    }

    private fun getToastMessage(imageButtonId: Int): String {
        return when (imageButtonId) {
            R.id.EIcon00 -> "Veggies clicked!"
            R.id.EIcon01 -> "Transport clicked!"
            R.id.EIcon02 -> "Shopping clicked!"
            R.id.EIcon03 -> "Charity clicked!"
            R.id.EIcon10 -> "Veggies clicked!"
            R.id.EIcon11 -> "Transport clicked!"
            R.id.EIcon12 -> "Shopping clicked!"
            R.id.EIcon13 -> "Settings clicked!"
            else -> "Clicked!"
        }
    }
}