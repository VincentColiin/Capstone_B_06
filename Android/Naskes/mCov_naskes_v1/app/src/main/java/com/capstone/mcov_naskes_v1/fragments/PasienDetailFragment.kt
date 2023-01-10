package com.capstone.mcov_naskes_v1.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.capstone.mcov_naskes_v1.R
import com.capstone.mcov_naskes_v1.databinding.FragmentPasienDetailBinding
import com.capstone.mcov_naskes_v1.models.Kondisi
import com.capstone.mcov_naskes_v1.models.User
import com.capstone.mcov_naskes_v1.viewModels.KondisiViewModel
import com.capstone.mcov_naskes_v1.viewModels.PasienViewModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class PasienDetailFragment(private val position: Int, private val uid: String?) : Fragment() {
    private lateinit var binding: FragmentPasienDetailBinding
    private val pasienViewModel: PasienViewModel by activityViewModels()
    private val kondisiViewModel: KondisiViewModel by activityViewModels()
    var pasienDetail: User? = null
    var kondisiDetail: Kondisi? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPasienDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val conditions: Array<String> = resources.getStringArray(R.array.kondisi)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, conditions)
        binding.autoKondisi.setAdapter(arrayAdapter)
        Handler(Looper.getMainLooper()).postDelayed({
            if (position == -1) {
                pasienDetail = pasienViewModel.getDataUID(uid!!)
                kondisiDetail = kondisiViewModel.getData(pasienDetail?.uid!!)
            } else {
                pasienDetail = pasienViewModel.getDataPosition(position)
                kondisiDetail = kondisiViewModel.getData(pasienDetail?.uid!!)
            }
            binding.pasienDetailNama.text = "${pasienDetail?.firstName} ${pasienDetail?.lastName}"
            val dropDownAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, conditions)
            binding.autoKondisi.setAdapter(dropDownAdapter)
            try {
                binding.autoKondisi.setText(conditions[kondisiDetail?.kondisi!!],false)
            }catch (err:Error){
                binding.autoKondisi.setText("nothing",false)
        }
//            binding.pasienDetailKondisi.text = kondisiDetail?.kondisi.toString()
            binding.pasienDetailBPM.text = "${kondisiDetail?.bpm} BPM"
            binding.pasienDetailSPO2.text = "${kondisiDetail?.spo2}%"
            val newDate = SimpleDateFormat("dd MMM yyyy-hh:mm a", Locale.getDefault())
            binding.pasienDetailLastChecked.text =
                newDate.format(kondisiDetail?.lastChecked!!).toString()
        }, 1000)

        binding.editMessage.addTextChangedListener {
            if(binding.editMessage.text.toString() == "")
                binding.SendButton.text = "Simpan"
            else
                binding.SendButton.text = "Kirim"
        }

        binding.SendButton.setOnClickListener {
            val kondisi = kondisiDetail!!
            if(binding.autoKondisi.text.toString() == conditions[0])
                kondisi.kondisi = 0
            else if (binding.autoKondisi.text.toString() == conditions[1])
                kondisi.kondisi = 1
            kondisiViewModel.updateData(kondisi)
            var message = binding.editMessage.text.toString()
            if(message.isEmpty()) message = "Kondisi di ubah"
            sendMessage(message)
        }

    }

    private fun sendMessage(message: String) {
        val userFcmToken: String = pasienDetail?.token!!
        val title: String = "Pesan dari Dokter"
        val uid: String = pasienDetail?.uid!!
        val postUrl: String = "https://fcm.googleapis.com/fcm/send"


        val requestQueue = Volley.newRequestQueue(activity);

        val mainObj: JSONObject = JSONObject()
        mainObj.put("to", userFcmToken);

        val notiObject = JSONObject();
        notiObject.put("content_avaiable", true);
        notiObject.put("title", title);
        notiObject.put("message", message)

        val dataObject = JSONObject()
        dataObject.put("content_avaiable", true);
        dataObject.put("title", title);
        dataObject.put("message", message)
        dataObject.put("uid", uid)

        mainObj.put("notification", notiObject)
        mainObj.put("data", dataObject)
        val request = object : JsonObjectRequest(Request.Method.POST, postUrl, mainObj,
            { response ->
            },
            { error ->
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] =
                    "Bearer AAAAQdFb9aM:APA91bE41PvQ-zrfeRf6MF0AIm01i4BXFqFvnKXTkm2-_0hR4zUMhqFPgcc8EVEkKBAoYoxTHp3iIDvkA_o2zrpzAD0b41_KJvzEboORtbOUxwbUl4k9MiJt8V6Tzxdc7jP0mIqSXUIE"
                return headers
            }
        }
        requestQueue.add(request);
    }
}