package com.example.wisewallet
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ManualEntryActivityIncome : AppCompatActivity() {
    lateinit var newPh:String
    lateinit var user:String
    private lateinit var titleTextView: TextView
    private lateinit var amountEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_income)

        val frameLayoutId = intent.getIntExtra("imageButtonId", -1)
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        newPh = sharedPreferences.getString("newPh", null).toString()
        user = sharedPreferences.getString("user", null).toString()

        Log.d("ManualEntryActivity", "FrameLayout ID: $frameLayoutId")
        lateinit var database: DatabaseReference
        titleTextView = findViewById(R.id.titleTextView)
        amountEditText = findViewById(R.id.amountEditText)
        saveButton = findViewById(R.id.saveButton)

        // Set the title based on the frameLayoutId
        when (frameLayoutId) {
            R.id.IIcon00 -> titleTextView.text = "Salary"
            R.id.IIcon01 -> titleTextView.text = "Bonus"
            R.id.IIcon02 -> titleTextView.text = "House Rent"
            R.id.IIcon10 -> titleTextView.text = "Interests"
            R.id.IIcon11 -> titleTextView.text = "Others"
            R.id.IIcon12 -> titleTextView.text = "Rupee"
            else -> titleTextView.text = "Transaction"
        }
        //Toast.makeText(this, "phone $phoneNumber and user $user category as ${titleTextView.text}", Toast.LENGTH_SHORT).show()

        saveButton.setOnClickListener {
            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("users")
            val ManualRef=database.getReference("Manual")
            val userRef = usersRef.child(newPh)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    // Create Manual and Expenses nodes if they don't exist
                    //Toast.makeText(applicationContext, "phone $phoneNumber and user $user category as ${titleTextView.text}", Toast.LENGTH_SHORT).show()
                    createYearMonthIncomeNodes(userRef)
                    Toast.makeText(applicationContext, "after addInc", Toast.LENGTH_SHORT).show()
                    finish()

                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }
    private fun createYearMonthIncomeNodes(userRef: DatabaseReference) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR).toString()
        val month = SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time).uppercase(Locale.getDefault())

        val yearRef = userRef.child(year)
        yearRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(yearSnapshot: DataSnapshot) {
                if (!yearSnapshot.exists()) {
                    yearRef.setValue(true) // Create the Year node
                }

                val monthRef = yearRef.child(month)
                monthRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(monthSnapshot: DataSnapshot) {
                        if (!monthSnapshot.exists()) {
                            monthRef.setValue(true) // Create the Month node
                        }

                        val expenseRef = monthRef.child("Income")
                        expenseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(expenseSnapshot: DataSnapshot) {
                                if (!expenseSnapshot.exists()) {
                                    expenseRef.setValue(true) // Create Expense node
                                }
                                addIncome(newPh, titleTextView.text.toString(), amountEditText.text.toString(), year, month)
                            }

                            override fun onCancelled(expenseError: DatabaseError) {
                                // Handle error
                                Toast.makeText(applicationContext, "Expense node creation/check failed", Toast.LENGTH_SHORT).show()
                            }
                        })

                    }

                    override fun onCancelled(monthError: DatabaseError) {
                        // Handle error
                        Toast.makeText(applicationContext, "Month node creation/check failed", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onCancelled(yearError: DatabaseError) {
                // Handle error
                Toast.makeText(applicationContext, "Year node creation/check failed", Toast.LENGTH_SHORT).show()
            }
        })
    }
    fun convertTimestampToDateTime(timestampMillis: Long): String {
        val date = Date(timestampMillis)
        val dateFormat = SimpleDateFormat("HH:mm:ss dd:MM:yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }
    fun addIncome(phoneNumber: String, category: String,amount: String,year:String,month:String) {
        val database = FirebaseDatabase.getInstance()
        val incomeRef = database.getReference("users").child(phoneNumber).child(year).child(month).child("Income").child(category)
        incomeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    incomeRef.setValue(true) // Create the Expenses node
                }
                val expenseMap = HashMap<String, Any>()
                expenseMap["amount"] =amount
                expenseMap["date"] = convertTimestampToDateTime(System.currentTimeMillis())
                incomeRef.push().setValue(expenseMap)
                //expensesRef.child("Amount").push().setValue(amount)
                //addExpense(phoneNumber, titleTextView.text.toString(),amountEditText.text.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Toast.makeText(applicationContext, "addExpense complete", Toast.LENGTH_SHORT).show()

            }
        })


        Toast.makeText(this, "category: $category", Toast.LENGTH_SHORT).show()
        //Toast.makeText(this, "amount: ${amount.toDouble()}", Toast.LENGTH_SHORT).show()

        /*val expenseMap = HashMap<String, Any>()
        expenseMap["date"] =  // Timestamp

        expensesRef.push().setValue(expenseMap)
            .addOnSuccessListener {
                // Success
                Log.d("Firebase", "Expense added successfully: $expenseMap")
                Toast.makeText(this, "Expense added successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                // Failure
                Log.e("Firebase", "Error adding expense: ${e.message}")
                Toast.makeText(this, "Error adding expense: ${e.message}", Toast.LENGTH_SHORT).show()
            }*/
    }
}