package com.example.cleantodoapp

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cleantodoapp.databinding.ActivityMainBinding
import com.example.cleantodoapp.domain.entity.Todo
import com.example.cleantodoapp.presentation.RecAdapter
import com.example.cleantodoapp.presentation.TodoViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var recAdapter: RecAdapter
    private val viewModel by lazy {
        ViewModelProvider(this)[TodoViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.addBtn.setOnClickListener {
            val title = binding.edtxt.text.toString()

            if (title.length > 3) {
                // 1. ViewModel orqali bazaga saqlash
                viewModel.addTodo(title)

                // 2. Bazadan yangilangan ro'yxatni olish
                val updatedList = viewModel.getTodos()
                // 3. Adapterni yangilash (submitList orqali)
                recAdapter.submitList(updatedList)

                // 4. Inputni tozalash
                binding.edtxt.text.clear()

            } else {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Xatolik !")
                dialog.setMessage("Vazifa nomi juda qisqa!")
                dialog.show()
            }
        }

        // Adapterni sozlash
        recAdapter = RecAdapter(
            onDelete = { todo ->
                viewModel.deleteTodo(todo) // O'chirish
                refreshData()
            },
            onEdit = { todo ->
                showEditDialog(todo) // Tahrirlash
            }
        )
        binding.recyclerView.adapter = recAdapter

    }

    private fun refreshData() {
        // toMutableList() qilish shart! Shunda ListAdapter yangi ro'yxat kelganini tushunadi
        val newList = viewModel.getTodos().toMutableList()
        recAdapter.submitList(newList)
    }

    fun showEditDialog(todo: Todo) {
        val editText = EditText(this)
        editText.setText(todo.title)

        AlertDialog.Builder(this)
            .setTitle("Vazifani tahrirlash")
            .setView(editText)
            .setPositiveButton("Saqlash") { _, _ ->
                val newTitle = editText.text.toString()
                viewModel.editTodo(todo) // ViewModel'ga yuboramiz
            }
            .setNegativeButton("Bekor qilish", null)
            .show()
    }
}