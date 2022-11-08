package com.example.fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fragment.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    /**
     * 0 - idle
     * 1 - frag A
     * 2 - frag B
     */
    var frag_state = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSwitch.setOnClickListener() {
            switchFragment()
        }

        binding.btnRemove.setOnClickListener() {
            removeFragment()
        }
    }

    private fun switchFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        when(frag_state){
            0 -> {
                val fragA = FragA()
                transaction.add(R.id.frameLayout, fragA)
                frag_state = 1
            }
            1 -> {
                val fragB = FragB()
                transaction.replace(R.id.frameLayout, fragB)
                frag_state = 2
            }
            2 -> {
                val fragA = FragA()
                val bundle = Bundle()
                bundle.putString("key", "From Main to Frag A")
                fragA.arguments = bundle
                transaction.replace(R.id.frameLayout, fragA)
                frag_state = 1
            }
        }
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun removeFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        val frameLayout = supportFragmentManager.findFragmentById(R.id.frameLayout)
        transaction.remove(frameLayout!!)
        transaction.commit()
    }
}