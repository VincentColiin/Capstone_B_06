package com.capstone.mcov_client_v1.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.capstone.mcov_client_v1.R
import com.capstone.mcov_client_v1.adapters.notifRecycleViewAdapter
import com.capstone.mcov_client_v1.databinding.FragmentNotifBinding
import com.capstone.mcov_client_v1.viewModels.NotifactionVM
import com.capstone.mcov_client_v1.viewModels.PasienVM


class NotifFragment : Fragment(),notifRecycleViewAdapter.ClickListener {

    private lateinit var binding: FragmentNotifBinding
    private lateinit var notifRecycleView:RecyclerView
    private lateinit var adapter: notifRecycleViewAdapter
    private val notifViewModel: NotifactionVM by activityViewModels()
    private val pasienViewModel: PasienVM by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotifBinding.inflate(inflater,container,false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notifViewModel.updateData(pasienViewModel.getData()?.uid!!)
        var listNotif = notifViewModel.getAllData()

        notifRecycleView = binding.notifRecycleView
        notifRecycleView.setHasFixedSize(false)
        notifRecycleView.layoutManager = LinearLayoutManager(activity)
        adapter = notifRecycleViewAdapter(listNotif,this@NotifFragment)
        notifRecycleView.adapter = adapter
        msi.attachToRecyclerView(notifRecycleView)

        binding.notifRefreshLayout.setOnRefreshListener {
            notifViewModel.updateData(pasienViewModel.getData()?.uid!!)
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
                viewHolder: ViewHolder, target: ViewHolder
            ): Boolean {
                return false
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                notifViewModel.removeData(notifViewModel.getData(viewHolder.adapterPosition)?.id!!,pasienViewModel.getData()?.uid!!)
                notifViewModel.updateData(pasienViewModel.getData()?.uid!!)
                Handler(Looper.getMainLooper()).postDelayed({
                    adapter.notifyDataSetChanged()
                },1000)
            }
        })

    override fun onClick(position: Int) {
        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.nav_host_fragment,NotifDetailFragment(position))
            ?.addToBackStack(null)
            ?.commit()
        activity?.onBackPressedDispatcher?.addCallback(this,object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                fragmentManager?.popBackStack()
            }

        })
    }
}
