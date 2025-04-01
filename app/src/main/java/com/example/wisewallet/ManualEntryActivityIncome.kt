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

                        val incomeRef = monthRef.child("Income")
                        incomeRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(expenseSnapshot: DataSnapshot) {
                                if (!expenseSnapshot.exists()) {
                                    incomeRef.setValue(true) // Create Expense node
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
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
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
                val incomeMap = HashMap<String, Any>()
                incomeMap["amount"] =amount
                incomeMap["date"] = convertTimestampToDateTime(System.currentTimeMillis())
                incomeRef.push().setValue(incomeMap)
                //expensesRef.child("Amount").push().setValue(amount)
                //addExpense(phoneNumber, titleTextView.text.toString(),amountEditText.text.toString())
                incomeRef.child("total").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val amountDouble = amount.toDoubleOrNull() ?: 0.0 //Handle amount being non double
                        if (snapshot.exists()) {
                            // "total" exists, update it
                            val existingTotal = snapshot.getValue(Double::class.java) ?: 0.0 // Handle null case

                            val newTotal = existingTotal + amountDouble
                            incomeRef.child("total").setValue(newTotal)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        updateIncomeTotal(phoneNumber, year, month)
                                    } else {
                                        Log.e("Firebase", "Error updating subcategory total: ${task.exception?.message}")
                                    }
                                }

                        } else {
                            // "total" doesn't exist, create it
                            incomeRef.child("total").setValue(amountDouble)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                        Log.e("Firebase", "Error checking total: ${error.message}")
                    }
                })
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
    private fun updateIncomeTotal(phoneNumber: String, year: String, month: String) {
        val database = FirebaseDatabase.getInstance()
        val expenseRef = database.getReference("users").child(phoneNumber).child(year).child(month).child("Income")

        expenseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var grandTotal = 0.0
                for (operationSnapshot in snapshot.children) {
                    val subTotal = operationSnapshot.child("total").getValue(Double::class.java) ?: 0.0
                    grandTotal += subTotal
                }
                expenseRef.child("total").setValue(grandTotal)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error updating Expense total: ${error.message}")
            }
        })
    }
}