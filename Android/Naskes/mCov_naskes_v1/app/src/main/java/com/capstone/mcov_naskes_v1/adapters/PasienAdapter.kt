package com.capstone.mcov_naskes_v1.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mcov_naskes_v1.R
import com.capstone.mcov_naskes_v1.models.User
import com.capstone.mcov_naskes_v1.viewModels.KondisiViewModel
import java.text.SimpleDateFormat
import java.util.*

class PasienAdapter(private val userList: List<User>,private val kondisiViewModel: KondisiViewModel, val listener: ClickListener): RecyclerView.Adapter<PasienAdapter.ListPasienViewHolder>() {
    inner class ListPasienViewHolder(userView: View): RecyclerView.ViewHolder(userView){
        val conditions = arrayOf("Sehat","Darurat")
        val userName: TextView = userView.findViewById(R.id.userCardName)
        val userTime: TextView = userView.findViewById(R.id.userCardTime)
        val userCondition: TextView = userView.findViewById(R.id.userCardCondition)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                listener.onClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListPasienViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_card,parent,false)
        return ListPasienViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ListPasienViewHolder, position: Int) {
        val kondisiDetail = kondisiViewModel.getData(userList[position].uid!!)
        val newDate = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault())
        holder.userName.text = "${userList[position].firstName} ${userList[position].lastName}"
        holder.userTime.text = newDate.format(kondisiDetail?.lastChecked!!).toString()
        holder.userCondition.text = holder.conditions[kondisiDetail?.kondisi!!]
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    interface ClickListener{
        fun onClick(position:Int)
    }
}