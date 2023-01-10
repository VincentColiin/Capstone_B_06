package com.capstone.mcov_client_v1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mcov_client_v1.R
import com.capstone.mcov_client_v1.models.Notification

class notifRecycleViewAdapter(private val notifList: List<Notification>, val listener: ClickListener): RecyclerView.Adapter<notifRecycleViewAdapter.notifViewHolder>() {
    inner class notifViewHolder(notifView: View):RecyclerView.ViewHolder(notifView){
        val notifTitle: TextView = notifView.findViewById(R.id.notifCardTitle)
        val notifBody: TextView = notifView.findViewById(R.id.notifCardBody)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                listener.onClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): notifViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.notification_card,parent,false)
        return notifViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: notifViewHolder, position: Int) {
        holder.notifTitle.text = notifList[position].title
        holder.notifBody.text = notifList[position].message
    }

    override fun getItemCount(): Int {
        return notifList.size
    }

    interface ClickListener{
        fun onClick(position:Int)
    }
}