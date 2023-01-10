package com.capstone.mcov_client_v1.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.capstone.mcov_client_v1.R
import com.capstone.mcov_client_v1.databinding.FragmentHomeBinding
import com.capstone.mcov_client_v1.viewModels.KondisiVM
import com.capstone.mcov_client_v1.viewModels.PasienVM
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val conditions = arrayOf("Stabil","Darurat")
    private val kondisiViewModel: KondisiVM by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Handler(Looper.getMainLooper()).postDelayed({
            val kondisi = kondisiViewModel.getData()
            binding.homeSpO2.text = "${kondisi?.spo2!!}%"
            binding.homeBPM.text = "${kondisi.bpm} BPM"
            when(kondisi.kondisi){
                0->{
                    binding.homeKondisiFrame.background = resources.getDrawable(R.mipmap.sehat,null)
                    binding.homeCondition.setTextColor(resources.getColor(R.color.sehat,null))
                }
                1->{
                    binding.homeKondisiFrame.background = resources.getDrawable(R.mipmap.sad,null)
                    binding.homeCondition.setTextColor(resources.getColor(R.color.darurat,null))
                }
            }
            binding.homeCondition.text = conditions[kondisi?.kondisi!!]
        },2000)
    }
}