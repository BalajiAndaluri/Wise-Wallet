package com.example.wisewallet.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.wisewallet.R


class Charts : Fragment() {

    private lateinit var spinner : Spinner
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_charts, container, false)
        spinner = view.findViewById(R.id.chart_menu)
        val listItems = listOf("Select ↓","Expense", "Income")
        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listItems)
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(parent!=null){
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    Toast.makeText(
                            requireContext(),
                    "Shows charts for $selectedItem",
                    Toast.LENGTH_SHORT
                    ).show()
                }


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }        /* { parent, view, position, id ->


        }

    }*/
        return view
    }
}