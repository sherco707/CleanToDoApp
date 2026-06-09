package com.example.cleantodoapp

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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

        setupRecyclerView()
        setupAddButton()
        refreshTodos()
    }

    private fun setupRecyclerView() {
        recAdapter = RecAdapter(
            onDelete = { todo ->
                viewModel.deleteTodo(todo)
                refreshTodos()
                Toast.makeText(this, "Vazifa o'chirildi", Toast.LENGTH_SHORT).show()
            },
            onEdit = { todo -> showEditDialog(todo) }
        )

        binding.recyclerView.apply {
            adapter = recAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupAddButton() {
        binding.addBtn.setOnClickListener {
            val title = binding.edtxt.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(this, "Vazifa nomini kiriting", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.addTodo(title)
            binding.edtxt.text?.clear()
            refreshTodos()
        }
    }

    private fun showEditDialog(todo: Todo) {
        val editText = EditText(this).apply {
            setText(todo.title)
            setSelection(text.length)
            hint = "Vazifani tahrirlash"
        }

        AlertDialog.Builder(this)
            .setTitle("Vazifani tahrirlash")
            .setView(editText)
            .setPositiveButton("Saqlash") { _, _ ->
                val newTitle = editText.text.toString().trim()

                if (newTitle.isEmpty()) {
                    Toast.makeText(this, "Vazifa nomi bo'sh bo'lmasin", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                viewModel.updateTodo(todo.copy(title = newTitle))
                refreshTodos()
            }
            .setNegativeButton("Bekor qilish", null)
            .show()
    }

    private fun refreshTodos() {
        recAdapter.submitList(viewModel.getTodos())
    }
}
