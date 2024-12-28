package com.example.wisewallet.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.wisewallet.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wisewallet.fragments.Charts
import com.example.wisewallet.fragments.Choose
import com.example.wisewallet.fragments.HomeFragment
import com.example.wisewallet.fragments.Me
import com.example.wisewallet.fragments.More
import androidx.lifecycle.findViewTreeViewModelStoreOwner


class HomeFragment : Fragment() {
    private lateinit var textDate:TextView
    private lateinit var buttonDate:Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val calendarBox=Calendar.getInstance()
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        textDate = view.findViewById(R.id.textDate)
        buttonDate = view.findViewById(R.id.buttonDate)
        val dateBox=DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            calendarBox.set(Calendar.YEAR,year)
            calendarBox.set(Calendar.MONTH,month)
            calendarBox.set(Calendar.DAY_OF_MONTH,day)
            updateText(calendarBox)//updates text to date selected from a date picker
        }
        buttonDate.setOnClickListener {
            DatePickerDialog(requireContext(),dateBox,calendarBox.get(Calendar.YEAR),calendarBox.get(Calendar.MONTH),calendarBox.get(Calendar.DAY_OF_MONTH)).show()

        }
        // Inflate the layout for this fragment
        return view
    }
    private fun updateText(calendar: Calendar){
        val dateFormat="yyyy"
        val simple = SimpleDateFormat(dateFormat, Locale.UK)
        textDate.text = simple.format(calendar.time)
        val monthFormat = "MMM" // Use "MMMM" for full month name (e.g., December) "MMM" for short name(eg. Jan)
        val monthSimple = SimpleDateFormat(monthFormat, Locale.UK)
        val monthName = monthSimple.format(calendar.time)
        buttonDate.text = monthName
    }



}