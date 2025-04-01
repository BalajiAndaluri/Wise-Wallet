package com.example.wisewallet

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class SplashActivity : AppCompatActivity(){
    private var hasAllPermissions=true
    private val permissionNameList= arrayOf(
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.SEND_SMS)
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

    private lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()
        setContentView(R.layout.activity_splash)
        //val sharedPrefs: SharedPreferences = getSharedPreferences("SMSPref", Context.MODE_PRIVATE)
        //val editor: SharedPreferences.Editor = sharedPrefs.edit()
        //editor.clear()
        //editor.apply() // Or editor.commit()
        handler=Handler()
        //clearAllExceptOne(this,"SMSPref","lastProcessedDate1")
        handler.postDelayed({
            val intent=Intent(this,NavigationActivity::class.java)
            startActivity(intent)
            finish()
        },6000)
        requestPermissions()
    }

    fun clearAllExceptOne(context: Context, sharedPrefsName: String, excludedKey: String) {
        val sharedPrefs: SharedPreferences = context.getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        val allKeys = sharedPrefs.all.keys.toList() // Get all keys

        for (key in allKeys) {
            if (key != excludedKey) {
                editor.remove(key) // Remove all except the excluded one
            }
        }
        editor.apply()
    }


}