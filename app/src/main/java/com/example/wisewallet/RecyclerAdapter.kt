package com.example.wisewallet

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.wisewallet.fragments.DataClass
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val entryDate: TextView = itemView.findViewById(R.id.entryDate)
    val entryCategory: TextView = itemView.findViewById(R.id.entryCategory)
    val entryImage: ImageView = itemView.findViewById(R.id.entryImage)
    val entryDesc: TextView = itemView.findViewById(R.id.entryDesc)
    val entryAmount: TextView = itemView.findViewById(R.id.entryAmount)
    val recCard: CardView = itemView.findViewById(R.id.recCard)
}

class RecyclerAdapter(private val context: Context, private val dataList: MutableList<DataClass>) :
    RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = dataList[position]

        holder.entryDate.text = data.date
        holder.entryCategory.text = data.category
        holder.entryAmount.text = data.amount.toString() // Convert Long to String
        holder.entryDesc.text = data.category // Or data.description if you have it.
        holder.entryImage.setImageResource(R.drawable.baseline_currency_rupee_24)
        try {
            val inputFormat = SimpleDateFormat("HH:mm:ss dd:MM:yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) // Example: 28 Feb 2025

            val date: Date = inputFormat.parse(data.date) ?: Date() //Handle null case.
            val formattedDate: String = outputFormat.format(date)
            holder.entryDate.text = formattedDate
        } catch (e: Exception) {
            Log.e("DateFormatting", "Error formatting date: ${e.message}")
            holder.entryDate.text = data.date // Display the original date if formatting fails
        }
    }

    override fun getItemCount(): Int = dataList.size
}