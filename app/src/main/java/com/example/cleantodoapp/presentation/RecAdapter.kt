package com.example.cleantodoapp.presentation

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cleantodoapp.databinding.ItemRecyclerViewBinding
import com.example.cleantodoapp.domain.entity.Todo
import java.text.DateFormat
import java.util.Date

class RecAdapter(
    private val onDelete: (Todo) -> Unit,
    private val onEdit: (Todo) -> Unit,
    private val onStatusChanged: (Todo, Boolean) -> Unit,
) : ListAdapter<Todo, RecAdapter.VH>(DiffCallback) {

    inner class VH(val itemBinding: ItemRecyclerViewBinding) : RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val todo = getItem(position)
        val context = holder.itemBinding.root.context

        holder.itemBinding.taskId.text = todo.title
        holder.itemBinding.taskTime.text = "Yozilgan: ${formatCreatedTime(todo.createdAt)}"
        holder.itemBinding.taskStatus.text = if (todo.isCompleted) {
            "Holat: bajarildi"
        } else {
            "Holat: bajarilmadi"
        }
        holder.itemBinding.taskStatus.setTextColor(
            ContextCompat.getColor(
                context,
                if (todo.isCompleted) android.R.color.holo_green_dark else android.R.color.holo_red_dark,
            ),
        )

        holder.itemBinding.taskId.paintFlags = if (todo.isCompleted) {
            holder.itemBinding.taskId.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.itemBinding.taskId.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        holder.itemBinding.doneCheckBox.setOnCheckedChangeListener(null)
        holder.itemBinding.doneCheckBox.isChecked = todo.isCompleted
        holder.itemBinding.doneCheckBox.setOnCheckedChangeListener { _, isChecked ->
            onStatusChanged(todo, isChecked)
        }

        holder.itemBinding.deletetxt.setOnClickListener { onDelete(todo) }
        holder.itemBinding.editxt.setOnClickListener { onEdit(todo) }
    }

    private fun formatCreatedTime(createdAt: Long): String {
        if (createdAt <= 0L) return "noma'lum"

        return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
            .format(Date(createdAt))
    }

    object DiffCallback : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem == newItem
        }
    }
}
