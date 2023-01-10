package com.capstone.mcov_client_v1.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.capstone.mcov_client_v1.databinding.FragmentNotifDetailBinding
import com.capstone.mcov_client_v1.viewModels.NotifactionVM

class NotifDetailFragment(private val position:Int) : Fragment() {
    private lateinit var binding:FragmentNotifDetailBinding
    private val notificationViewModel: NotifactionVM by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotifDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val notifDetail = notificationViewModel.getData(position)
        binding.notifDetailTitle.text = notifDetail?.title
        binding.notifDetailBody.text = notifDetail?.message
    }
}