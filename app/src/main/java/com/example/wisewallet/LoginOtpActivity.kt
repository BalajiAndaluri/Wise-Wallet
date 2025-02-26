package com.example.wisewallet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wisewallet.R.*
import java.util.Random

import android.Manifest
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import org.json.JSONObject
import java.io.IOException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginOtpActivity : AppCompatActivity() {

    private lateinit var verifyButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var otpEditText: EditText
    private var generatedOtp: String?=null
    private lateinit var otpString:String
    lateinit var phoneNumber: String
    lateinit var username:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_otp) // Replace with your layout

        verifyButton = findViewById(id.login_next_btn) // Replace with your Button ID
        resultTextView = findViewById(id.result_text) // Replace with your result TextView ID
        otpEditText = findViewById(id.login_otp) // Replace with your EditText ID
         phoneNumber= intent.getStringExtra("phoneNumber").toString()
        Toast.makeText(applicationContext, "Phone Number is $phoneNumber", Toast.LENGTH_SHORT).show()

        if (phoneNumber != null) {
            generateOtp(phoneNumber)
            Toast.makeText(applicationContext, "generatedOtp() done $phoneNumber", Toast.LENGTH_SHORT).show()
        } else {
            generatedOtp = "000000"
            println("Phone number not found in Intent")
        }



        verifyButton.setOnClickListener {
            verifyOtp()
        }
        Toast.makeText(applicationContext, "otp_verified", Toast.LENGTH_SHORT).show()
    }

    private fun checkOrCreatePhoneNumber(phoneNumber: String) {
        Toast.makeText(applicationContext, "Checking/Creating phone number...", Toast.LENGTH_SHORT).show()

        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")
        val userRef = usersRef.child(phoneNumber)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                runOnUiThread {
                    if (snapshot.exists()) {
                        // Phone number exists, go to main activity
                        Toast.makeText(applicationContext, "Phone number found!", Toast.LENGTH_SHORT).show()
                        username = snapshot.child("username").getValue(String::class.java).toString()
                        Toast.makeText(applicationContext, "User $username", Toast.LENGTH_SHORT).show()
                        if (username != null) {
                            goToLoginUsernameActivity(phoneNumber, username) // Go to main activity with username
                        } else {
                            //Username is null. Handle this case, perhaps go to username collection.
                            goToUsernameCollectionPage(phoneNumber)
                        }
                    } else {
                        // Phone number does not exist, go to username collection page
                        goToUsernameCollectionPage(phoneNumber)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                runOnUiThread {
                    Log.e("Firebase", "Database error: ${error.message}")
                    Toast.makeText(applicationContext, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                    //Handle error
                    goToUsernameCollectionPage(phoneNumber)
                }
            }
        })
    }

    private fun goToUsernameCollectionPage(phoneNumber: String) {
        val intent = Intent(this, LoginUsernameActivity::class.java)
        intent.putExtra("phoneNumber", phoneNumber)
        startActivity(intent)
    }

    private fun goToLoginUsernameActivity(phoneNumber: String, username: String) {
        val intent = Intent(this, LoginUsernameActivity::class.java)
        intent.putExtra("phoneNumber", phoneNumber)
        intent.putExtra("username", username)
        startActivity(intent)
    }


    private val SEND_SMS_PERMISSION_REQUEST = 7

    private fun generateOtp(phoneNumber: String) { //Pass the phone number
        Toast.makeText(applicationContext, "generatedOtp() called $phoneNumber", Toast.LENGTH_SHORT).show()
        sendOtpSms(phoneNumber)
    }

    private fun sendOtpSms(phoneNumber: String) {
        val random = Random()
        val otp = StringBuilder()
        for (i in 0 until 6) {
            otp.append(random.nextInt(10))
        }
        otpString = otp.toString()
        Toast.makeText(applicationContext, "OTP thayar!", Toast.LENGTH_SHORT).show()
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, "Your OTP is: $otpString", null, null)
            Toast.makeText(applicationContext, "OTP sent successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "SMS failed, please try again.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SEND_SMS_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, send the SMS
                //get the phone number from the edit text or where ever you are storing it.
                val phoneNumber = findViewById<EditText>(R.id.phoneNumberEditText).text.toString()
                sendOtpSms(phoneNumber)

            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }*/

//Example of how to call the function.
//generateOtp("+15551234567")

    private fun verifyOtp() {
        val userEnteredOtp = otpEditText.text.toString()

        if (userEnteredOtp == otpString) {
            resultTextView.text = "OTP Verified!"
            checkOrCreatePhoneNumber(phoneNumber)
            // Do something if OTP matches
        } else {
            resultTextView.text = "OTP Verification Failed."
            // Do something else if OTP does not match
        }
    }
}