package com.example.wisewallet
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    private val permissionId_read=100
    private var hasAllPermissions=true
    private val permissionNameList= arrayOf(
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.SEND_SMS)
    private val requestPermissionLauncher=registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){isGranted ->
        if(isGranted){
            Toast.makeText(this,"Granted!",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this,"Denied!",Toast.LENGTH_SHORT).show()
        }

    }
    private val requestMultiplePermissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                if (isGranted) {
                    // Permission is granted. Continue the action that required the permission
                    Log.d("Permission", "$permissionName granted")

                } else {
                    Log.d("Permission", "$permissionName denied")
                    hasAllPermissions = false
                }
            }
        }
    private fun requestPermissions() {
        requestMultiplePermissionsLauncher.launch(permissionNameList)
    }

    lateinit var phoneNumber:String
    lateinit var user:String
    private lateinit var bottomNavigationView: BottomNavigationView
    private var isFirstLaunch: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)//initial layout
        requestPermissionLauncher.launch(android.Manifest.permission.READ_SMS)
        requestPermissionLauncher.launch(android.Manifest.permission.SEND_SMS)
        requestPermissionLauncher.launch(android.Manifest.permission.RECEIVE_SMS)

        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        isFirstLaunch = sharedPrefs.getBoolean("isFirstLaunch", true)
        if (isFirstLaunch) {
            // It's the first launch, request permissions
            requestPermissions()

            // Update the first launch flag in SharedPreferences
            val editor = sharedPrefs.edit()
            editor.putBoolean("isFirstLaunch", false)
            editor.apply()  // or editor.commit()
        }
        checkAndRequestSmsPermission()
        // Check if all permissions are granted before proceeding
        //Date picker moved to HomeFragment


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
    private fun checkAndRequestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), permissionId_read)
        } else {
            retrieveTransactionSMS()
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionId_read) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                retrieveTransactionSMS()
            } else {
                Log.e("MainActivity", "SMS permission denied")
            }
        }
    }

    private fun retrieveTransactionSMS() {
        val smsMessages = getTransactionSMS(this)
        for (message in smsMessages) {
            Log.d("MainActivity", "SMS: $message")
            // Display the message in your UI or process it as needed
        }
    }

    fun getTransactionSMS(context: Context): MutableList<String> {
        val sharedPrefs: SharedPreferences = context.getSharedPreferences("SMSPref", Context.MODE_PRIVATE)
        val lastProcessedDate = sharedPrefs.getLong("lastUsedDate", 0L) // 0L means no date stored yet
        val transactionMessages = mutableListOf<String>()
        val uri = android.net.Uri.parse("content://sms/inbox")
        val projection = arrayOf("_id", "address", "body", "date")
        val selection = " LOWER(body) LIKE ? or LOWER(body) LIKE ? or LOWER(body) LIKE ?"
        val selectionArgs = arrayOf("a/c *____%", "a/c X____%", "a/c X%X____%")
        val sortOrder = "date ASC"

        val cursor: android.database.Cursor? = context.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
        cursor?.use {
            val bodyIndex = it.getColumnIndex("body")
            val dateIndex = it.getColumnIndex("date")
            var latestProcessedDate = lastProcessedDate
            while (it.moveToNext()) {
                val messageBody = it.getString(bodyIndex)
                val messageDate = it.getLong(dateIndex)
                transactionMessages.add(messageBody)
                if (messageDate > latestProcessedDate) {
                    // Extract year and month from message date
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = messageDate
                    val year = calendar.get(Calendar.YEAR).toString()
                    val month = SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)
                        .uppercase(Locale.getDefault())
                        latestProcessedDate = maxOf(latestProcessedDate, messageDate)
                    // Check for keywords and add to Firebase accordingly
                    if (messageBody.contains("credited", true)) {
                        addToFirebase("Income", "Credited", messageBody, year, month)
                    } else if (messageBody.contains("debited", true)) {
                        addToFirebase("Expense", "Debited", messageBody, year, month)
                    }

                }
            }
            val editor = sharedPrefs.edit()
            editor.putLong("lastUsedDate", latestProcessedDate)
            editor.apply()


        }
        return transactionMessages
    }
    private fun addToFirebase(transactionType: String, operation: String, message: String, year: String, month: String) {
        val database = FirebaseDatabase.getInstance()
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        phoneNumber = sharedPreferences.getString("phoneNumber", null).toString()
        user = sharedPreferences.getString("user", null).toString()
        val newPh=sharedPreferences.getString("newPh", null).toString()
        // Extract amount and date from the message
        val amount = extractAmount(message)
        val date = extractDate(message)

        if (amount != null && date != null) {
            val transactionRef = database.getReference("users").child(newPh).child(year).child(month).child(transactionType).child(operation).push()
            val transactionData = hashMapOf(
                "amount" to amount,
                "date" to date
            )
            transactionRef.setValue(transactionData)
                .addOnSuccessListener {
                    // Data added successfully
                }
                .addOnFailureListener {
                    // Handle errors
                }
            val operationRef = database.getReference("users").child(newPh).child(year).child(month).child(transactionType).child(operation)
            operationRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var totalAmount = 0.0
                    for (transactionSnapshot in snapshot.children) {
                        val amountStr = transactionSnapshot.child("amount").getValue(String::class.java)
                            ?.toDoubleOrNull()
                            ?: 0.0
                        totalAmount += amountStr
                    }
                    operationRef.child("total").setValue(totalAmount)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Subcategory total updated successfully, now update Expense total
                                updateExpenseTotal(newPh, year, month)
                                updateIncomeTotal(newPh, year, month)
                            } else {
                                Log.e("Firebase", "Error updating subcategory total: ${task.exception?.message}")
                            }
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error calculating total: ${error.message}")
                }
            })
        } else {
            // Handle cases where amount or date extraction fails
            Log.e("MainActivity", "Failed to extract amount or date from message: $message")
        }
    }
    private fun extractAmount(message: String): String? {
        val pattern: Pattern = Pattern.compile("Rs[.:\\s]?(\\d+\\.\\d{2})")
        val matcher: Matcher = pattern.matcher(message)
        return if (matcher.find()) {
            matcher.group(1)
        } else null
    }

    private fun extractDate(message: String): String? {
        val pattern: Pattern = Pattern.compile("on (\\d{2}-\\d{2}-\\d{4})")
        val matcher: Matcher = pattern.matcher(message)
        return if (matcher.find()) {
            matcher.group(1)
        } else null
    }
    private fun updateExpenseTotal(phoneNumber: String, year: String, month: String) {
        val database = FirebaseDatabase.getInstance()
        val expenseRef = database.getReference("users").child(phoneNumber).child(year).child(month).child("Expense")

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
    private fun replaceFragment(fragment:Fragment){         //new fragment  current fragment
        //replaces current fragment with new fragment
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
        //then the changes applied to fragment container are commited
    }
}