package com.example.wisewallet
import android.Manifest
import android.content.pm.PackageManager
import android.content.Intent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.app.DatePickerDialog
import android.os.Bundle
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
import androidx.lifecycle.lifecycleScope
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
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
            }
            else {
                // Permission denied, handle appropriately (e.g., show an explanation)
            }
        }
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)//initial layout
        requestPermissionLauncher.launch(Manifest.permission.READ_SMS)
        // Check if all permissions are granted before proceeding
        //4 jan Not working
        //permission dialog is not opening
       /* if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_SMS)){
                val builder = AlertDialog.Builder(this)
                builder.setMessage("This App require to read and parse messages to get transactions data.")
                    .setTitle("Permissions Required")
                    .setCancelable(false)
                    .setPositiveButton("Ok"){dialog,_->
                        ActivityCompat.requestPermissions(this,permissionNameList,permissionId)
                        dialog.dismiss()
                        builder.show()
                    }


                }
        }
        else{
            ActivityCompat.requestPermissions(this,permissionNameList,permissionId)
        }*/
        //29dec
        /* else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, request it
                requestPermissionLauncher.launch(Manifest.permission.SEND_SMS)
            } else{
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                    // Permission not granted, request it
                    requestPermissionLauncher.launch(Manifest.permission.RECEIVE_SMS)
                } else{
                    requestPermissionLauncher.launch(Manifest.permission.READ_SMS)
                }
            }
            // Permission already granted, proceed with reading SMS
            // ... your code to read SMS messages here ...
        }*/
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