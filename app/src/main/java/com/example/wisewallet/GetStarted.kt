package com.example.wisewallet

import com.example.wisewallet.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.wisewallet.MainActivity

class GetStarted : AppCompatActivity() {
    private lateinit var startButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding_visual)

        startButton = findViewById<Button>(R.id.startButton)

        startButton.setOnClickListener(View.OnClickListener {
            val i = Intent(
                this@GetStarted,
                MainActivity::class.java
            )
            startActivity(i)
            finish()
        })
    }
}