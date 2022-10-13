package com.example.change_view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.change_view.databinding.ActivitySubBinding

class SubActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_sub)

        val binding = ActivitySubBinding.inflate(layoutInflater);

        setContentView(binding.root)

        binding.btnClose.setOnClickListener {
            finish()
        }
    }
}