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

class ManualEntryActivity : AppCompatActivity() {
    lateinit var phoneNumber:String
    lateinit var user:String
    private lateinit var titleTextView: TextView
    private lateinit var amountEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual)

        val frameLayoutId = intent.getIntExtra("imageButtonId", -1)
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        phoneNumber = sharedPreferences.getString("phoneNumber", null).toString()
        user = sharedPreferences.getString("user", null).toString()

        Log.d("ManualEntryActivity", "FrameLayout ID: $frameLayoutId")
        lateinit var database: DatabaseReference
        titleTextView = findViewById(R.id.titleTextView)
        amountEditText = findViewById(R.id.amountEditText)
        saveButton = findViewById(R.id.saveButton)

        // Set the title based on the frameLayoutId
        when (frameLayoutId) {
            R.id.EIcon00 -> titleTextView.text = "Veggies"
            R.id.EIcon01 -> titleTextView.text = "Transport"
            R.id.EIcon02 -> titleTextView.text = "Shopping"
            R.id.EIcon03 -> titleTextView.text = "Charity"
            R.id.EIcon10 -> titleTextView.text = "Veggies"
            R.id.EIcon11 -> titleTextView.text = "Transport"
            R.id.EIcon12 -> titleTextView.text = "Shopping"
            R.id.EIcon13 -> titleTextView.text = "Settings"
            else -> titleTextView.text = "Transaction"
        }
        Toast.makeText(this, "phone $phoneNumber and user $user category as ${titleTextView.text}", Toast.LENGTH_SHORT).show()

        saveButton.setOnClickListener {
            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("users")
            val userRef = usersRef.child(phoneNumber)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    // Create Manual and Expenses nodes if they don't exist
                    Toast.makeText(applicationContext, "phone $phoneNumber and user $user category as ${titleTextView.text}", Toast.LENGTH_SHORT).show()
                    createManualAndExpensesNodes(userRef)
                    Toast.makeText(applicationContext, "after addExp", Toast.LENGTH_SHORT).show()
                    finish()

                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }
    private fun createManualAndExpensesNodes(userRef: DatabaseReference) {
        val manualRef = userRef.child("Manual")
        manualRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    manualRef.setValue(true) // Create the Manual node
                }

                val expensesRef = manualRef.child("Expenses")
                expensesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) {
                            expensesRef.setValue(true) // Create the Expenses node
                        }
                        addExpense(phoneNumber, titleTextView.text.toString(),amountEditText.text.toString())
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                        Toast.makeText(applicationContext, "addExpense complete", Toast.LENGTH_SHORT).show()

                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
    fun addExpense(phoneNumber: String, category: String,amount: String) {
        val database = FirebaseDatabase.getInstance()
        val expensesRef = database.getReference("users").child(phoneNumber).child("Manual").child("Expenses").child(category)
        expensesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    expensesRef.setValue(true) // Create the Expenses node
                }
                val expenseMap = HashMap<String, Any>()
                expenseMap["amount"] = amount
                expenseMap["date"] = Date().time
                expensesRef.push().setValue(expenseMap)
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