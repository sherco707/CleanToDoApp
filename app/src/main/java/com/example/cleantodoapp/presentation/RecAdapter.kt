package com.example.cleantodoapp.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cleantodoapp.databinding.ItemRecyclerViewBinding
import com.example.cleantodoapp.domain.entity.Todo


class RecAdapter(
    private val onDelete: (Todo) -> Unit,
    private val onEdit: (Todo) -> Unit
) : ListAdapter<Todo, RecAdapter.VH>(DiffCallback) {

    inner class VH(val itemBinding: ItemRecyclerViewBinding) : RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        // ListAdapter ichidagi ro'yxatdan elementni olish uchun getItem() ishlatiladi
        val todo = getItem(position)

        holder.itemBinding.taskId.text = todo.title

        // O'chirish
        holder.itemBinding.deletetxt.setOnClickListener { onDelete(todo) }

        // Tahrirlash
        holder.itemBinding.editxt.setOnClickListener { onEdit(todo) }
    }

    object DiffCallback : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            // ID bo'yicha solishtirish
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            // Content bo'yicha solishtirish
            return oldItem == newItem
        }
    }
}