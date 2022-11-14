package com.example.recyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recyclerview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding

    private val data:MutableList<Member> = mutableListOf()
    var i=4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initialize()
        refreshRecyclerView()

        binding.fab.setOnClickListener{
            val string="Member$i"
            i++
            data.add(Member(string))
            refreshRecyclerView()
        }
    }

    private fun initialize(){
        with(data){
            add(Member("Member1"))
            add(Member("Member2"))
            add(Member("Member3"))
        }
    }

    private fun refreshRecyclerView(){
        val adapter=MyAdapter()
        adapter.listData = data
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }
}