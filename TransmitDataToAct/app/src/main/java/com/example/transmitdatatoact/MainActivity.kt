package com.example.transmitdatatoact

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import com.example.transmitdatatoact.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mainBinding.btnGoSubActivity.setOnClickListener() {
            val text = mainBinding.editText.text.toString()
            val intent = Intent(this, SubActivity::class.java)
            intent.putExtra("Data", text)
            startActivity(intent)
        }

    }
}