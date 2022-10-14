package com.example.transimitdatabetactivies

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.app.Activity
import android.content.Intent
import com.example.transimitdatabetactivies.databinding.ActivitySubBinding

class SubActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnOkay.setOnClickListener {
            val name = binding.editText.text.toString()
            val returnIntent = Intent()
            returnIntent.putExtra("returnValue", name)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()

        }
    }
}