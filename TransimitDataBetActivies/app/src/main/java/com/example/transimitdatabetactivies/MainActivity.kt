package com.example.transimitdatabetactivies

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Intent
import com.example.transimitdatabetactivies.databinding.ActivityMainBinding
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {

    private lateinit var getResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Register callback for receiving result
        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                println("get name")
                println(it.data)
                val name = it.data?.getStringExtra("returnValue")
                binding.textView.text = name
            } else {
                println("error?")
            }
        }

        binding.button.setOnClickListener {
            val intent = Intent(this, SubActivity::class.java)
            getResult.launch(intent)
        }
    }
}