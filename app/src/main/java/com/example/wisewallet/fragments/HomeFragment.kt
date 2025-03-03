package com.example.wisewallet.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wisewallet.R
import com.example.wisewallet.RecyclerAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class DataClass(
    val amount: String,
    val date: String,
    val category: String
)

class HomeFragment : Fragment() {
    private lateinit var phoneNumber: String
    private lateinit var textDate: TextView
    private lateinit var buttonDate: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: MutableList<DataClass>
    private lateinit var adapter: RecyclerAdapter
    private lateinit var databaseReference: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var userRef: DatabaseReference
    private lateinit var valueEventListener: ValueEventListener // Declare listener here

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val calendarBox = Calendar.getInstance()
        dataList = mutableListOf()
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val sharedPreferences: SharedPreferences =
            requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        phoneNumber = sharedPreferences.getString("phoneNumber", null).toString()
        val newPh = sharedPreferences.getString("newPh", null).toString()
        Toast.makeText(requireContext(), "Number: $newPh", Toast.LENGTH_SHORT).show()
        textDate = view.findViewById(R.id.textDate)
        buttonDate = view.findViewById(R.id.buttonDate)
        databaseReference = FirebaseDatabase.getInstance()
        usersRef = databaseReference.getReference("users")
        userRef = usersRef.child(newPh)
        val dateBox = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendarBox.set(Calendar.YEAR, year)
            calendarBox.set(Calendar.MONTH, month)
            calendarBox.set(Calendar.DAY_OF_MONTH, day)
            updateText(calendarBox, userRef)
        }
        buttonDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateBox,
                calendarBox.get(Calendar.YEAR),
                calendarBox.get(Calendar.MONTH),
                calendarBox.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        recyclerView = view.findViewById(R.id.RecycleView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        adapter = RecyclerAdapter(requireContext(), dataList)
        recyclerView.adapter = adapter

        updateText(calendarBox, userRef) // Load data for the current month initially

        return view
    }

    private fun fetchDataFromFirebase(year: String, month: String, userRef: DatabaseReference) {
        Toast.makeText(requireContext(), "Number1: $phoneNumber", Toast.LENGTH_SHORT).show()
        val userEntriesReference = userRef.child(year).child(month)
        Toast.makeText(requireContext(), "$userEntriesReference", Toast.LENGTH_LONG).show()

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (isAdded && context != null) {
                    Toast.makeText(requireContext(), "Number2: $phoneNumber", Toast.LENGTH_SHORT).show()
                    dataList.clear()
                    for (categorySnapshot in snapshot.children) {
                        Toast.makeText(requireContext(), "cat: $categorySnapshot", Toast.LENGTH_SHORT).show()
                        for (subCategorySnapshot in categorySnapshot.children) {
                            for (entrySnapshot in subCategorySnapshot.children) {
                                Toast.makeText(requireContext(), "called!", Toast.LENGTH_LONG).show()
                                if (entrySnapshot.hasChild("amount") && entrySnapshot.hasChild("date")) {
                                    val amount = entrySnapshot.child("amount").getValue(String::class.java) ?: ""
                                    val date = entrySnapshot.child("date").getValue(String::class.java) ?: ""
                                    val category = categorySnapshot.key ?: ""
                                    val subCategory = subCategorySnapshot.key ?: ""
                                    Toast.makeText(requireContext(), "$amount and $date", Toast.LENGTH_LONG).show()
                                    val data = DataClass(amount, date, "$category - $subCategory")
                                    dataList.add(data)
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    Log.w("HomeFragment", "Fragment detached, not updating data.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                if (isAdded && context != null) {
                    Toast.makeText(requireContext(), "Failed: ${error.toException()}", Toast.LENGTH_LONG).show()
                    Log.e("Firebase", "Failed to read value.", error.toException())
                } else {
                    Log.w("HomeFragment", "Fragment detached, not showing error.")
                }
            }
        }
        userEntriesReference.addValueEventListener(valueEventListener)
    }

    private fun updateText(calendar: Calendar, userRef: DatabaseReference) {
        val dateFormat = "yyyy"
        val simpleYear = SimpleDateFormat(dateFormat, Locale.UK)
        val year = simpleYear.format(calendar.time)
        textDate.text = year

        val monthFormat = "MMM"
        val monthSimple = SimpleDateFormat(monthFormat, Locale.UK)
        val monthName = monthSimple.format(calendar.time).uppercase(Locale.UK)
        buttonDate.text = monthName

        fetchDataFromFirebase(year, monthName, userRef)
    }

    override fun onDetach() {
        super.onDetach()
        if (::userRef.isInitialized && ::valueEventListener.isInitialized) {
            userRef.removeEventListener(valueEventListener)
        }
    }
}




/*package com.example.wisewallet.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.wisewallet.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wisewallet.RecyclerAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.internal.applyConnectionSpec
import java.net.URLDecoder

data class DataClass(
    val amount: String,
    val date: String,
    val category: String
)

class HomeFragment : Fragment() {
    lateinit var phoneNumber: String

    private lateinit var textDate: TextView
    private lateinit var buttonDate: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: MutableList<DataClass>
    private lateinit var adapter: RecyclerAdapter
    private lateinit var databaseReference: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var userRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val calendarBox = Calendar.getInstance()
        dataList = mutableListOf()
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val sharedPreferences: SharedPreferences =
            requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        phoneNumber = sharedPreferences.getString("phoneNumber", null).toString()
        val newPh = sharedPreferences.getString("newPh", null).toString()
        Toast.makeText(requireContext(),"Number: $newPh",Toast.LENGTH_SHORT).show()
        textDate = view.findViewById(R.id.textDate)
        buttonDate = view.findViewById(R.id.buttonDate)
        databaseReference = FirebaseDatabase.getInstance()
        usersRef = databaseReference.getReference("users")
        userRef = usersRef.child(newPh)
        val dateBox = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendarBox.set(Calendar.YEAR, year)
            calendarBox.set(Calendar.MONTH, month)
            calendarBox.set(Calendar.DAY_OF_MONTH, day)
            updateText(calendarBox,userRef)
        }
        buttonDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateBox,
                calendarBox.get(Calendar.YEAR),
                calendarBox.get(Calendar.MONTH),
                calendarBox.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        recyclerView = view.findViewById(R.id.RecycleView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        adapter = RecyclerAdapter(requireContext(), dataList)
        recyclerView.adapter = adapter

        updateText(calendarBox, userRef) // Load data for the current month initially

        return view
    }

    private fun fetchDataFromFirebase(year: String, month: String, userRef:DatabaseReference) {
        Toast.makeText(requireContext(),"Number1: $phoneNumber",Toast.LENGTH_SHORT).show()
        val userEntriesReference = userRef.child(year).child(month)
        Toast.makeText(requireContext(), "$userEntriesReference", Toast.LENGTH_LONG).show()
        userEntriesReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Toast.makeText(requireContext(),"Number2: $phoneNumber",Toast.LENGTH_SHORT).show()
                //Toast.makeText(requireContext(),"here, $dataList[1]",Toast.LENGTH_SHORT).show()
                //dataList.clear()
                for (categorySnapshot in snapshot.children) { //Income/Expense
                    Toast.makeText(requireContext(),"cat: $categorySnapshot",Toast.LENGTH_SHORT).show()
                    for (subCategorySnapshot in categorySnapshot.children) { // Iterates through "Charity", "Shopping", etc.
                        for (entrySnapshot in subCategorySnapshot.children) { //iterates unique ids
                            Toast.makeText(requireContext(), "called!", Toast.LENGTH_LONG).show()
                            if (entrySnapshot.hasChild("amount") && entrySnapshot.hasChild("date")) { // Check for amount and date
                                val amount = entrySnapshot.child("amount").getValue(String::class.java) ?: ""
                                val date = entrySnapshot.child("date").getValue(String::class.java) ?: ""
                                val category = categorySnapshot.key ?: "" // "Income" or "Expense"
                                val subCategory = subCategorySnapshot.key ?: ""
                                Toast.makeText(requireContext(), "$amount and $date", Toast.LENGTH_LONG).show()
                                val data = DataClass(
                                    amount,
                                    date,
                                    "$category - $subCategory"
                                )

                                dataList.add(data)
                            }
                        }
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),"Failed: ${error.toException()}",Toast.LENGTH_LONG).show()
                Log.e("Firebase", "Failed to read value.", error.toException())
            }
        })
    }

    private fun updateText(calendar: Calendar, userRef: DatabaseReference) {
        val dateFormat = "yyyy"
        val simpleYear = SimpleDateFormat(dateFormat, Locale.UK)
        val year = simpleYear.format(calendar.time)
        textDate.text = year

        val monthFormat = "MMM"
        val monthSimple = SimpleDateFormat(monthFormat, Locale.UK)
        val monthName = monthSimple.format(calendar.time).uppercase(Locale.UK)
        buttonDate.text = monthName

        fetchDataFromFirebase(year, monthName,userRef)
    }
}*/




/*package com.example.wisewallet.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class DataClass(
    val amount: Long, // Use Long for amounts
    val date: String,
    val category: String
)
class HomeFragment : Fragment() {
    lateinit var phoneNumber:String
    private lateinit var textDate:TextView
    private lateinit var buttonDate:Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: MutableList<DataClass>
    private lateinit var adapter: RecyclerAdapter
    private lateinit var databaseReference: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val calendarBox=Calendar.getInstance()
        dataList= mutableListOf()
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        phoneNumber = sharedPreferences.getString("phoneNumber", null).toString()
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
        adapter = RecyclerAdapter(requireContext(), dataList)
        recyclerView.adapter = adapter
        databaseReference = FirebaseDatabase.getInstance().getReference("users") // Replace "entries" with your node name
        return view
        /*dataList.add(
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
        return view*/
    }
    private fun fetchDataFromFirebase(year:String,month:String,phone_number:String) {
        val userEntriesReference = databaseReference.child(phone_number).child(year).child(month)

        userEntriesReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for (categorySnapshot in snapshot.children) {//Income/Expense
                    for (subCategorySnapshot in categorySnapshot.children) { // Iterates through "Credited", "veggies", "taxi", etc.
                        for(entrySnapshot in subCategorySnapshot.children){//iterates unique ids
                            for (sub2Snapshot in entrySnapshot.children) {
                                val amount =
                                    sub2Snapshot.child("amount").getValue(Long::class.java) ?: 0L
                                val date =
                                    sub2Snapshot.child("date").getValue(String::class.java) ?: ""
                                /*val description =
                                    entrySnapshot.child("description").getValue(String::class.java)
                                        ?: ""*/
                                val category = categorySnapshot.key ?: "" // "Income" or "Expense"
                                val subCategory = subCategorySnapshot.key
                                    ?: "" // "Credited", "veggies", "taxi", etc.

                                val data = DataClass(
                                    amount,
                                    date,
                                    "$category - $subCategory",
                                ) // Combined category and subcategory

                                dataList.add(data)
                            }
                    }
                }
                adapter.notifyDataSetChanged()
            }
        })
        /*databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear() // Clear the list before adding new data
                for (dataSnapshot in snapshot.children) {
                    val entryDesc = dataSnapshot.child("description").getValue(String::class.java) ?: ""
                    val entryValue = dataSnapshot.child("value").getValue(String::class.java) ?: ""
                    val entryImage = R.drawable.baseline_currency_rupee_24 // Or fetch from Firebase if needed

                    val data = DataClass(entryImage, entryDesc, entryValue)
                    dataList.add(data)
                }
                adapter.notifyDataSetChanged() // Notify adapter of data changes
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to read value.", error.toException())
            }
        })*/
    }
    private fun updateText(calendar: Calendar){
        val dateFormat="yyyy"
        val simple = SimpleDateFormat(dateFormat, Locale.UK)
        val monthFormat = "MMM" // Use "MMMM" for full month name (e.g., December) "MMM" for short name(eg. Jan)
        //textDate.text = simple.format(calendar.time)




        val simpleYear = SimpleDateFormat(dateFormat, Locale.UK)
        val year = simpleYear.format(calendar.time)
        textDate.text = year

        val monthSimple = SimpleDateFormat(monthFormat, Locale.UK)
        val monthName = monthSimple.format(calendar.time)
        buttonDate.text = monthName
        fetchDataFromFirebase(year, monthName,phoneNumber)
    }



}*/