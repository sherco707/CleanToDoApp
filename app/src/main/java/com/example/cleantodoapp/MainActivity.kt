package com.example.cleantodoapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cleantodoapp.databinding.ActivityMainBinding
import com.example.cleantodoapp.presentation.RecAdapter

class MainActivity : AppCompatActivity() {
    lateinit var adapter: RecAdapter
    lateinit var binding: ActivityMainBinding

    val list = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        binding.addBtn.setOnClickListener {
            list.add(binding.edtxt.text.toString())
            adapter = RecAdapter(list)
            binding.recyclerView.adapter = adapter
        }

    }
}
