package com.example.wisewallet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.wisewallet.LoginOtpActivity
import com.hbb20.CountryCodePicker

class LoginPhoneNumberActivity : AppCompatActivity() {
    lateinit var countryCodePicker: CountryCodePicker
    lateinit var phoneInput: EditText
    lateinit var sendOtpBtn: Button
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        countryCodePicker = findViewById(R.id.login_countrycode)
        phoneInput = findViewById(R.id.login_mobile_number)
        var phoneNumber: String
        sendOtpBtn = findViewById(R.id.send_otp_btn)
        progressBar = findViewById(R.id.login_progress_bar)

        progressBar.setVisibility(View.GONE)

        countryCodePicker.registerCarrierNumberEditText(phoneInput)
        sendOtpBtn.setOnClickListener(View.OnClickListener setOnClickListener@{ v: View? ->
            if (!countryCodePicker.isValidFullNumber()) {
                phoneInput.setError("Phone number not valid")
                return@setOnClickListener
            }
            phoneNumber = countryCodePicker.fullNumberWithPlus

            val intent=Intent(this,LoginOtpActivity::class.java)
            intent.putExtra("phoneNumber", phoneNumber)
            startActivity(intent)
            finish()

            Toast.makeText(applicationContext, "OTP sent successfully!", Toast.LENGTH_SHORT).show()
        })
    }
}