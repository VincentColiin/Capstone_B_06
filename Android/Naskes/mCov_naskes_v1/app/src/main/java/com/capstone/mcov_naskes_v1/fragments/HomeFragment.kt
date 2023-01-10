package com.capstone.mcov_naskes_v1.fragments

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mcov_naskes_v1.R
import com.capstone.mcov_naskes_v1.adapters.OverviewAdapter
import com.capstone.mcov_naskes_v1.adapters.PasienAdapter
import com.capstone.mcov_naskes_v1.databinding.FragmentHomeBinding
import com.capstone.mcov_naskes_v1.databinding.FragmentListPasienBinding
import com.capstone.mcov_naskes_v1.viewModels.KondisiViewModel
import com.capstone.mcov_naskes_v1.viewModels.PasienViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging

class HomeFragment : Fragment(), OverviewAdapter.ClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var overviewRecyclerView: RecyclerView
    private lateinit var adapter: OverviewAdapter
    private val pasienViewModel: PasienViewModel by activityViewModels()
    private val kondisiViewModel: KondisiViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Handler(Looper.getMainLooper()).postDelayed({
            val listPasien = pasienViewModel.getAllData()
            binding.totalText.text = listPasien.size.toString()
            var totalDarurat = 0
            for(pasien in listPasien){
                kondisiViewModel.getData(pasien.uid!!)
                if(kondisiViewModel.getData(pasien.uid!!)?.kondisi == 1) totalDarurat++
            }
            binding.daruratText.text = totalDarurat.toString()
            overviewRecyclerView = binding.overviewRcycleView
            overviewRecyclerView.setHasFixedSize(false)
            overviewRecyclerView.layoutManager = LinearLayoutManager(activity)
            adapter = OverviewAdapter(listPasien,kondisiViewModel,this@HomeFragment,resources.getDrawable(R.drawable.green_circle,null),resources.getDrawable(R.drawable.red_circle,null))
            overviewRecyclerView.adapter = adapter
            pasienViewModel.refreshData()
            kondisiViewModel.refreshData()
            Handler(Looper.getMainLooper()).postDelayed({
                adapter.notifyDataSetChanged()
            },500)
        },1000)


    }

    override fun onClick(position: Int,uid:String) {
        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.nav_host_fragment, PasienDetailFragment(position, uid))
            ?.addToBackStack(null)
            ?.commit()
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                fragmentManager?.popBackStack()
            }

        })
    }

}