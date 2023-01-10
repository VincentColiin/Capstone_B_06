package com.capstone.mcov_naskes_v1.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.mcov_naskes_v1.R
import com.capstone.mcov_naskes_v1.adapters.NotificationAdapter
import com.capstone.mcov_naskes_v1.databinding.FragmentNotificationBinding
import com.capstone.mcov_naskes_v1.viewModels.NotificationViewModel

class ListNotificationFragment : Fragment(),NotificationAdapter.ClickListener {

    private lateinit var binding: FragmentNotificationBinding
    private lateinit var notifRecycleView: RecyclerView
    private lateinit var adapter: NotificationAdapter
    private val notifViewModel: NotificationViewModel by activityViewModels()
//    private val pasienViewModel: PasienVM by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(inflater,container,false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notifViewModel.updateData()
        var listNotif = notifViewModel.getAllData()
        notifRecycleView = binding.listNotificationRecycleView
        notifRecycleView.setHasFixedSize(false)
        notifRecycleView.layoutManager = LinearLayoutManager(activity)
        Log.d("notif",listNotif.toString())
        adapter = NotificationAdapter(listNotif,this@ListNotificationFragment)
        notifRecycleView.adapter = adapter
        msi.attachToRecyclerView(notifRecycleView)
        notifViewModel.updateData()
        Handler(Looper.getMainLooper()).postDelayed({
            adapter.notifyDataSetChanged()
        },1000)


        binding.notifRefreshLayout.setOnRefreshListener {
            notifViewModel.updateData()
            listNotif = notifViewModel.getAllData()
            Handler(Looper.getMainLooper()).postDelayed({
                adapter.notifyDataSetChanged()
                binding.notifRefreshLayout.isRefreshing = false
            },1000)
        }

    }

    var msi = ItemTouchHelper(
        object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or  ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                notifViewModel.removeData(notifViewModel.getData(viewHolder.adapterPosition)?.id!!)
                Handler(Looper.getMainLooper()).postDelayed({
                    adapter.notifyDataSetChanged()
                },1000)
            }
        })

    override fun onClick(position: Int) {
        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.nav_host_fragment,PasienDetailFragment(position,notifViewModel.getData(position)?.uid!!))
            ?.addToBackStack(null)
            ?.commit()
        activity?.onBackPressedDispatcher?.addCallback(this,object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                fragmentManager?.popBackStack()
            }

        })
    }
}