package com.example.remember

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.remember.models.MemoryModel

class MemoryAdapter(
    private val context: Context,
    private val memoryList: List<MemoryModel>,
    val onClick: (MemoryModel) -> Unit
) : RecyclerView.Adapter<MemoryAdapter.MemoryViewHolder>() {

    inner class MemoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView : CardView = itemView.findViewById(R.id.memory_card)
        val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
        val categoryTextView: TextView = itemView.findViewById(R.id.textViewCategory)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_memory_view, parent, false)
        return MemoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemoryViewHolder, position: Int) {
        val memory = memoryList[position]

        holder.titleTextView.text = memory.title
        holder.categoryTextView.text = memory.category.name

        try {
            if (memory.image.isNullOrEmpty()) {
                holder.imageView.setImageResource(R.drawable.default_memory_photo)
            } else {
                val bitmap = BitmapFactory.decodeFile(memory.image)
                if (bitmap != null) {
                    holder.imageView.setImageBitmap(bitmap)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        holder.cardView.setOnClickListener {
           onClick(memory)
        }
    }

    override fun getItemCount(): Int = memoryList.size
}