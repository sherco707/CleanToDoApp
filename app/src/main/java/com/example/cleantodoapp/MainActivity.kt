package com.example.cleantodoapp

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cleantodoapp.databinding.ActivityMainBinding
import com.example.cleantodoapp.domain.entity.Todo
import com.example.cleantodoapp.presentation.RecAdapter
import com.example.cleantodoapp.presentation.TodoUiState
import com.example.cleantodoapp.presentation.TodoViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var lastShownMessageId: Long? = null

    private val recAdapter = RecAdapter(
        onDelete = ::deleteTodo,
        onEdit = ::showEditDialog,
        onStatusChanged = ::updateTodoCompletion,
    )

    private val viewModel: TodoViewModel by viewModels {
        (application as CleanTodoApplication).appContainer.todoViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupAddButton()
        observeUiState()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = recAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }
    }

    private fun setupAddButton() {
        binding.addBtn.setOnClickListener {
            val title = binding.edtxt.text.toString()
            viewModel.addTodo(title)
            binding.edtxt.text?.clear()
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::render)
            }
        }
    }

    private fun render(uiState: TodoUiState) {
        binding.addBtn.isEnabled = !uiState.isLoading
        binding.recyclerView.isVisible = !uiState.isLoading
        recAdapter.submitList(uiState.todos)

        uiState.userMessage?.let { message ->
            if (message.id != lastShownMessageId) {
                lastShownMessageId = message.id
                Toast.makeText(this, message.text, Toast.LENGTH_SHORT).show()
                viewModel.userMessageShown()
            }
        }
    }

    private fun deleteTodo(todo: Todo) {
        viewModel.deleteTodo(todo)
    }

    private fun updateTodoCompletion(todo: Todo, isCompleted: Boolean) {
        if (todo.isCompleted != isCompleted) {
            viewModel.updateTodo(todo.copy(isCompleted = isCompleted))
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
                viewModel.updateTodo(todo.copy(title = editText.text.toString()))
            }
            .setNegativeButton("Bekor qilish", null)
            .show()
    }
}
