package com.example.wisewallet

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.wisewallet.fragments.Charts
import com.example.wisewallet.fragments.Choose
import com.example.wisewallet.fragments.HomeFragment
import com.example.wisewallet.fragments.Me
import com.example.wisewallet.fragments.More
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    //private lateinit var textDate:TextView
    //private lateinit var buttonDate:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)//initial layout
        //26dec Datepicker
        /*textDate=findViewById(R.id.textDate)
        buttonDate=findViewById(R.id.buttonDate)
        val calendarBox=Calendar.getInstance()
        val dateBox=DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            calendarBox.set(Calendar.YEAR,year)
            calendarBox.set(Calendar.MONTH,month)
            calendarBox.set(Calendar.DAY_OF_MONTH,day)
            updateText(calendarBox)//updates text to date selected from a date picker
        }
        buttonDate.setOnClickListener {
            DatePickerDialog(this,dateBox,calendarBox.get(Calendar.YEAR),calendarBox.get(Calendar.MONTH),calendarBox.get(Calendar.DAY_OF_MONTH)).show()

        }*/


        //25dec Bottom navigation icons to their respective fragments
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){//1.home fragment
                R.id.bottom_home-> {
                    replaceFragment(HomeFragment())
                    true
                }
                //2.charts
                R.id.charts-> {
                    replaceFragment(Charts())
                    true
                }
                //3.Choose
                R.id.choose-> {
                    Toast.makeText(this,"Choose any one option to add transaction manually",Toast.LENGTH_SHORT).show()
                    replaceFragment(Choose())
                    true
                }
                //4.More
                R.id.more-> {
                    replaceFragment(More())
                    true
                }
                //5.Me (profile)
                R.id.Me-> {
                    replaceFragment(Me())
                    true
                }
                else -> false
            }

        }
        replaceFragment(HomeFragment())//default

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    //26dec specified to home fragment only
   /* private fun updateText(calendar: Calendar){
        val dateFormat="yyyy"
        val simple = SimpleDateFormat(dateFormat, Locale.UK)
        textDate.text = simple.format(calendar.time)
    }*/
//25dec
    private fun replaceFragment(fragment:Fragment){         //new fragment  current fragment
        //replaces current fragment with new fragment
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
        //then the changes applied to fragment container are commited
    }
}