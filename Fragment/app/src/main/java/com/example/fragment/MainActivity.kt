package com.example.fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fragment.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    /**
     * 0 - idle
     * 1 - frag A
     * 2 - frag B
     */
    private var fragState = 0


    private val fragA = FragA()
    private val fragB = FragB()
    private val fragC = FragC()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSwitch.setOnClickListener {
            switchFragment()
        }

        binding.btnRemove.setOnClickListener {
            removeFragment()
        }
    }

    private fun switchFragment() {
        val transaction = supportFragmentManager.beginTransaction()

        when(fragState){
            0 -> {
                transaction.add(R.id.frameLayoutA, fragA)
                fragState = 1
            }
            1 -> {
                transaction.replace(R.id.frameLayoutA, fragB)
                fragState = 2
            }
            2 -> {
                val bundle = Bundle()
                bundle.putString("key", "From Main to Frag A")
                fragA.arguments = bundle
                transaction.replace(R.id.frameLayoutA, fragA)
                fragState = 1
            }
        }
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun removeFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        val frameLayout = supportFragmentManager.findFragmentById(R.id.frameLayoutA)
        transaction.remove(frameLayout!!)
        transaction.commit()
    }

    fun openFragmentOnFrameLayoutB(int: Int){
        val transaction = supportFragmentManager.beginTransaction()
        val frameLayout = supportFragmentManager.findFragmentById(R.id.frameLayoutB)

        when(int){
            1 -> transaction.add(R.id.frameLayoutB, fragC)
            2 -> transaction.remove(frameLayout!!)
        }
        transaction.commit()
    }

    fun changeReqFromFragA(inputString: String){
        fragC.changeText(inputString)
    }

}