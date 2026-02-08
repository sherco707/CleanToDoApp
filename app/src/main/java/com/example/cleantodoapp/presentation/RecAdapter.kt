package com.example.cleantodoapp.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cleantodoapp.databinding.ItemRecyclerViewBinding

class RecAdapter (val list:List<String>): RecyclerView.Adapter<RecAdapter.VH>(){
    inner class VH(val itemBinding: ItemRecyclerViewBinding): RecyclerView.ViewHolder(itemBinding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemRecyclerViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.itemBinding.taskId.text = list[position]
    }

    override fun getItemCount(): Int {
        return list.size
    }


}