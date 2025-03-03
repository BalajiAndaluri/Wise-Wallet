package com.example.wisewallet
import android.Manifest
import android.content.pm.PackageManager
import android.content.Intent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Telephony
import android.util.Log
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlinx.coroutines.*
import java.util.Locale
class MainActivity : AppCompatActivity() {
    private val permissionId_read=100
    private val permissionId_receive=101
    private val permissionId_send=102
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
        Toast.makeText(applicationContext, "read_sms permission", Toast.LENGTH_SHORT).show()
        requestPermissionLauncher.launch(android.Manifest.permission.SEND_SMS)
        Toast.makeText(applicationContext, "send_sms permission", Toast.LENGTH_SHORT).show()
        requestPermissionLauncher.launch(android.Manifest.permission.RECEIVE_SMS)
        Toast.makeText(applicationContext, "receive_sms permission", Toast.LENGTH_SHORT).show()

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
                    //Toast.makeText(this,"Choose any one option to add transaction manually",Toast.LENGTH_SHORT).show()
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
        val sharedPrefs: SharedPreferences = context.getSharedPreferences("SMSPrefs", Context.MODE_PRIVATE)
        val lastProcessedDate = sharedPrefs.getLong("lastProcessedDate", 0L) // 0L means no date stored yet
        val transactionMessages = mutableListOf<String>()
        val uri = android.net.Uri.parse("content://sms/inbox")
        val projection = arrayOf("_id", "address", "body", "date")
        val selection = "LOWER(body) LIKE ? or LOWER(body) LIKE ? or LOWER(body) LIKE ? or LOWER(body) LIKE ? or LOWER(body) LIKE ?"
        val selectionArgs = arrayOf("%credited%", "%debited%", "a/c *____%", "a/c X____%", "a/c X%X____%")
        val sortOrder = "date DESC"

        val cursor: android.database.Cursor? = context.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
        cursor?.use {
            val bodyIndex = it.getColumnIndex("body")
            val dateIndex = it.getColumnIndex("date")
            var latestProcessedDate = lastProcessedDate
            while (it.moveToNext()) {
                val messageBody = it.getString(bodyIndex)
                val messageDate = it.getLong(dateIndex)
                transactionMessages.add(messageBody)
                if (messageDate > lastProcessedDate) {
                    // Extract year and month from message date
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = messageDate
                    val year = calendar.get(Calendar.YEAR).toString()
                    val month = SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)
                        .uppercase(Locale.getDefault())

                    // Check for keywords and add to Firebase accordingly
                    if (messageBody.contains("credited", true)) {
                        addToFirebase("Income", "Credited", messageBody, year, month)
                    } else if (messageBody.contains("debited", true)) {
                        addToFirebase("Expense", "Debited", messageBody, year, month)
                    }
                    latestProcessedDate = maxOf(latestProcessedDate, messageDate)
                }
            }
            val editor = sharedPrefs.edit()
            editor.putLong("lastProcessedDate", latestProcessedDate)
            editor.apply()
        }
        return transactionMessages
    }

    private fun addToFirebase(transactionType: String, operation: String, message: String, year: String, month: String) {
        val database = FirebaseDatabase.getInstance()
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        phoneNumber = sharedPreferences.getString("phoneNumber", null).toString()
        user = sharedPreferences.getString("user", null).toString()
        //val phoneNumber = "your_phone_number" // Replace with actual phone number
        val transactionRef = database.getReference("users").child(phoneNumber).child(year).child(month).child(transactionType).child(operation).push()
        val transactionData = hashMapOf(
            "operation" to operation,
            "message" to message
        )
        transactionRef.setValue(transactionData)
            .addOnSuccessListener {
                // Data added successfully
            }
            .addOnFailureListener {
                // Handle errors
            }
    }

    /*fun getTransactionSMS(context: android.content.Context): List<String> {
        val transactionMessages = mutableListOf<String>()
        val uri = android.net.Uri.parse("content://sms/inbox")
        val projection = arrayOf("_id", "address", "body", "date")
        val selection = "LOWER(body) LIKE ? or LOWER(body) LIKE ? or LOWER(body) LIKE ? or LOWER(body) LIKE ? or LOWER(body) LIKE ?"
        val selectionArgs = arrayOf("%credited%", "%debited%","a/c *____%","a/c X____%","a/c X%X____%")
        val sortOrder = "date DESC"

        val cursor: android.database.Cursor? = context.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
        cursor?.use {
            val bodyIndex = it.getColumnIndex("body")
            while (it.moveToNext()) {
                val messageBody = it.getString(bodyIndex)
                transactionMessages.add(messageBody)
            }
        }
        return transactionMessages
    }*/

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