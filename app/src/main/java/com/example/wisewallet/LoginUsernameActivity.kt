/*package com.example.wisewallet

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wisewallet.MainActivity

class LoginUsernameActivity : AppCompatActivity() {
    lateinit var usernameInput: EditText
    lateinit var letMeInBtn: Button
    lateinit var progressBar: ProgressBar
    lateinit var phoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        usernameInput = findViewById(R.id.login_username)
        letMeInBtn = findViewById(R.id.login_let_me_in_btn)
        progressBar = findViewById(R.id.login_progress_bar)

        phoneNumber = intent.extras?.getString("phone").toString()
        usernameFromPrefs // Load username from SharedPreferences

        letMeInBtn!!.setOnClickListener { v: View? -> setUsername() }
    }

    fun setUsername() {
        val username = usernameInput!!.text.toString()
        if (TextUtils.isEmpty(username) || username.length < 3) {
            usernameInput!!.error = "Username length should be at least 3 chars"
            return
        }
        setInProgress(true)
        saveUsernameToPrefs(username) // Save username to SharedPreferences
        setInProgress(false)
        val intent = Intent(
            this@LoginUsernameActivity,
            MainActivity::class.java
        )
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    val usernameFromPrefs: Unit
        get() {
            setInProgress(true)
            val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            val username = prefs.getString(USERNAME_KEY, null)
            if (username != null) {
                usernameInput!!.setText(username)
            }
            setInProgress(false)
        }

    fun saveUsernameToPrefs(username: String?) {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(USERNAME_KEY, username)
        editor.apply()
        Toast.makeText(this, "Username Saved", Toast.LENGTH_SHORT).show()
    }

    fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            progressBar!!.visibility = View.VISIBLE
            letMeInBtn!!.visibility = View.GONE
        } else {
            progressBar!!.visibility = View.GONE
            letMeInBtn!!.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val PREFS_NAME = "MyPrefsFile"
        private const val USERNAME_KEY = "username"
    }
}*/
package com.example.wisewallet

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wisewallet.MainActivity
import com.google.firebase.database.FirebaseDatabase
import android.util.Log

class LoginUsernameActivity : AppCompatActivity() {
    lateinit var usernameInput: EditText
    var user: String=""
    lateinit var letMeInBtn: Button
    lateinit var progressBar: ProgressBar
    lateinit var phoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        usernameInput = findViewById(R.id.login_username)
        letMeInBtn = findViewById(R.id.login_let_me_in_btn)
        progressBar = findViewById(R.id.login_progress_bar)

        phoneNumber = intent.getStringExtra("phoneNumber").toString() //Retrieve phone number from intent
        user=intent.getStringExtra("username").toString()
        if(user.isNotEmpty()&& user!=null){
            usernameInput.setText(user)
        }
        letMeInBtn.setOnClickListener { v: View? -> setUsername() }
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("phoneNumber", phoneNumber)
        editor.putString("user", user)
        editor.apply()
    }

    fun setUsername() {
        val username = usernameInput.text.toString()
        if (TextUtils.isEmpty(username) || username.length < 3) {
            usernameInput.error = "Username length should be at least 3 chars"
            return
        }
        setInProgress(true)
        saveUsernameToFirebase(phoneNumber, username) // Save username to Firebase
    }

    fun saveUsernameToFirebase(phoneNumber: String, username: String) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")
        val userRef = usersRef.child(phoneNumber)

        userRef.child("username").setValue(username)
            .addOnSuccessListener {
                runOnUiThread {
                    Toast.makeText(this, "Username stored!", Toast.LENGTH_SHORT).show()
                    setInProgress(false)
                    val intent = Intent(
                        this@LoginUsernameActivity,
                        MainActivity::class.java
                    )
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.putExtra("phoneNumber", phoneNumber) //Pass phone number if needed in next activity
                    intent.putExtra("username", username) //Pass username to next activity
                    startActivity(intent)
                }
            }
            .addOnFailureListener { e ->
                runOnUiThread {
                    Log.e("Firebase", "Error storing username: ${e.message}")
                    Toast.makeText(this, "Error storing username: ${e.message}", Toast.LENGTH_SHORT).show()
                    setInProgress(false)
                }
            }
    }

    fun setInProgress(inProgress: Boolean) {
        runOnUiThread{
            if (inProgress) {
                progressBar.visibility = View.VISIBLE
                letMeInBtn.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                letMeInBtn.visibility = View.VISIBLE
            }
        }
    }
}