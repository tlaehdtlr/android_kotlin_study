package com.example.transmitdatatoact

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.transmitdatatoact.databinding.ActivitySubBinding

class SubActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val subBinding = ActivitySubBinding.inflate(layoutInflater)
        setContentView(subBinding.root)

        subBinding.textView.text = intent.getStringExtra("Data")

        subBinding.btnClose.setOnClickListener() {
            finish()
        }
    }
}