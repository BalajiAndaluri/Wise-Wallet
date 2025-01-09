package com.example.wisewallet
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.wisewallet.fragments.DataClass

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val recImage: ImageView = itemView.findViewById(R.id.entryImage)
    val recValue: TextView = itemView.findViewById(R.id.entryValue)
    val recDesc: TextView = itemView.findViewById(R.id.entryDesc)
    val recCard: CardView = itemView.findViewById(R.id.recCard)
}
class RecyclerAdapter(private val context: Context, private val dataList: MutableList<DataClass>) :
    RecyclerView.Adapter<MyViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = dataList[position] // Use direct property access

        holder.recImage.setImageResource(data.entryImage)
        holder.recValue.text=data.entryValue
        holder.recDesc.text=data.entryDesc

    }

    override fun getItemCount(): Int = dataList.size
}