package com.example.change_view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Intent
import com.example.change_view.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        // 1. initialize binding
        val binding = ActivityMainBinding.inflate(layoutInflater);

        // 2. display layout
        setContentView(binding.root)

        // 3. access button
        binding.btnGoSubActivity.setOnClickListener {
            val intent = Intent(this, SubActivity::class.java)
            startActivity(intent)
        }
    }
}