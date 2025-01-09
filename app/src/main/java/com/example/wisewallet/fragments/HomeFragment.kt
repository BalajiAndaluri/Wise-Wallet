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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wisewallet.RecyclerAdapter

data class DataClass(
    val entryImage: Int,
    val entryDesc: String,
    val entryValue:String
)
class HomeFragment : Fragment() {
    private lateinit var textDate:TextView
    private lateinit var buttonDate:Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: MutableList<DataClass>
    private lateinit var adapter: RecyclerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val calendarBox=Calendar.getInstance()
        dataList= mutableListOf()
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        textDate = view.findViewById(R.id.textDate)
        buttonDate = view.findViewById(R.id.buttonDate)
        val dateBox=DatePickerDialog.OnDateSetListener { _/*datePicker*/, year, month, day ->
            calendarBox.set(Calendar.YEAR,year)
            calendarBox.set(Calendar.MONTH,month)
            calendarBox.set(Calendar.DAY_OF_MONTH,day)
            updateText(calendarBox)//updates text to date selected from a date picker
        }
        buttonDate.setOnClickListener {
            DatePickerDialog(requireContext(),dateBox,calendarBox.get(Calendar.YEAR),calendarBox.get(Calendar.MONTH),calendarBox.get(Calendar.DAY_OF_MONTH)).show()

        }
        recyclerView=view.findViewById(R.id.RecycleView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        dataList.add(
            DataClass(
                R.drawable.baseline_currency_rupee_24,
                "Salary",
                "75000"
            )
        )
        dataList.add(
            DataClass(
                R.drawable.baseline_currency_rupee_24,
                "Interest",
                "5000"
            )
        )
        dataList.add(
            DataClass(
                R.drawable.baseline_currency_rupee_24,
                "Bonus",
                "25000"
            )
        )
        adapter = RecyclerAdapter(requireContext(), dataList)
        recyclerView.adapter = adapter


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