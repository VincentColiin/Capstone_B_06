package com.capstone.mcov_naskes_v1.fragments

import android.annotation.SuppressLint
import android.os.Binder
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
import com.capstone.mcov_naskes_v1.adapters.PasienAdapter
import com.capstone.mcov_naskes_v1.databinding.FragmentListPasienBinding
import com.capstone.mcov_naskes_v1.viewModels.KondisiViewModel
import com.capstone.mcov_naskes_v1.viewModels.PasienViewModel

class ListPasienFragment : Fragment(),PasienAdapter.ClickListener {

    private lateinit var binding: FragmentListPasienBinding
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var adapter: PasienAdapter
    private val pasienViewModel: PasienViewModel by activityViewModels()
    private val kondisiViewModel: KondisiViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListPasienBinding.inflate(inflater,container,false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userRecyclerView = binding.listPasienRecycleView
        userRecyclerView.setHasFixedSize(false)
        userRecyclerView.layoutManager = LinearLayoutManager(activity)
        var listNotif = pasienViewModel.getAllData()
        adapter = PasienAdapter(listNotif,kondisiViewModel,this@ListPasienFragment)
        userRecyclerView.adapter = adapter
        pasienViewModel.refreshData()
        kondisiViewModel.refreshData()
        Handler(Looper.getMainLooper()).postDelayed({
            adapter.notifyDataSetChanged()
        },1000)
//        msi.attachToRecyclerView(notifRecycleView)

        binding.listPasienRefresh.setOnRefreshListener {
            pasienViewModel.refreshData()
            listNotif = pasienViewModel.getAllData()
            Handler(Looper.getMainLooper()).postDelayed({
                adapter.notifyDataSetChanged()
                binding.listPasienRefresh.isRefreshing = false
            },1000)
        }
    }


    override fun onClick(position: Int) {
        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.nav_host_fragment,PasienDetailFragment(position,null))
            ?.addToBackStack(null)
            ?.commit()
        activity?.onBackPressedDispatcher?.addCallback(this,object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                fragmentManager?.popBackStack()
            }

        })
    }
}