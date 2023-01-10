package com.capstone.mcov_naskes_v1.adapters

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mcov_naskes_v1.R
import com.capstone.mcov_naskes_v1.models.User
import com.capstone.mcov_naskes_v1.viewModels.KondisiViewModel
import java.util.*

class OverviewAdapter(private val userList: List<User>, private val kondisiViewModel: KondisiViewModel, val listener: ClickListener,val greenCircle: Drawable, val redCircle: Drawable): RecyclerView.Adapter<OverviewAdapter.ListOverviewViewHolder>() {
    inner class ListOverviewViewHolder(userView: View) : RecyclerView.ViewHolder(userView) {
        val userName: TextView = userView.findViewById(R.id.overviewNamaPasien)
        val conditionCircle:FrameLayout = userView.findViewById(R.id.circle)
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                val uid = userList[position].uid!!
                listener.onClick(position,uid)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListOverviewViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.overview_user_list, parent, false)
        return ListOverviewViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ListOverviewViewHolder, position: Int) {
        val kondisiDetail = kondisiViewModel.getData(userList[position].uid!!)
        when(kondisiDetail?.kondisi){
            0->holder.conditionCircle.background = greenCircle
            1->holder.conditionCircle.background = redCircle
        }
        holder.userName.text = "${userList[position].firstName} ${userList[position].lastName}"
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    interface ClickListener {
        fun onClick(position: Int,uid:String)
    }
}