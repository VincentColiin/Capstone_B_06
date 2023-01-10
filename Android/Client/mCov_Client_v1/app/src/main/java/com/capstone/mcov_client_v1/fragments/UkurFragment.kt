package com.capstone.mcov_client_v1.fragments

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.volley.Header
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.capstone.mcov_client_v1.R
import com.capstone.mcov_client_v1.databinding.FragmentUkurBinding
import com.capstone.mcov_client_v1.models.Kondisi
import com.capstone.mcov_client_v1.models.TimeFormat
import com.capstone.mcov_client_v1.models.dataSensor
import com.capstone.mcov_client_v1.viewModels.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.JsonArray
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.nio.FloatBuffer
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class UkurFragment : Fragment() {

    private lateinit var binding: FragmentUkurBinding
    private val conditions = arrayOf("Stabil", "Darurat")

    private lateinit var listDataSensor: MutableList<dataSensor>
    private val kondisiViewModel: KondisiVM by activityViewModels()
    private val pasienViewModel: PasienVM by activityViewModels()
    private val naskesViewModel: NaskesVM by activityViewModels()
    private lateinit var ukurProgrss: ProgressBar
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUkurBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ukurProgrss = binding.ukurProgress
        naskesViewModel.updateData()
        kondisiViewModel.updateData(pasienViewModel.getData()?.uid!!)
        firestore = FirebaseFirestore.getInstance()
        listDataSensor = mutableListOf()
        updateText()
        registeredEvents()
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    private fun updateText() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.bpmText.text = "${kondisiViewModel.getData()?.bpm} BPM"
            binding.spo2Text.text = "${kondisiViewModel.getData()?.spo2}%"
            if (kondisiViewModel.getData()?.kondisi == -1) {
                binding.kondisiText.text = "---"
                binding.lastCheckText.text = "---"
            } else {
                when(kondisiViewModel.getData()?.kondisi){
                    0-> {
                        binding.ukurConditionFrame.background = resources.getDrawable(R.mipmap.sehat,null)
                        binding.kondisiText.setTextColor(resources.getColor(R.color.sehat,null))
                    }
                    1->{
                        binding.ukurConditionFrame.background = resources.getDrawable(R.mipmap.sad,null)
                        binding.kondisiText.setTextColor(resources.getColor(R.color.darurat,null))
                    }
                }
                binding.kondisiText.text = conditions[kondisiViewModel.getData()?.kondisi!!]
//                getLastChecked(kondisiViewModel.getData()?.lastChecked!!,serverTimeViewModel.getData()?.timestamp!!)
                val newDate = SimpleDateFormat("dd MMM yyyy - hh:mm a", Locale.getDefault())
                binding.lastCheckText.text =
                    newDate.format(kondisiViewModel.getData()?.lastChecked!!).toString()
            }
        },1000)


    }

//    private fun getLastChecked(lastChecked: Date, currentDate: Date) {
//        val diff = currentDate.time.minus(lastChecked.time)
//        val seconds = diff/1000%60
//        val minutes = diff/(1000*60)%60
//        val hours = diff/(1000*60*60)%60
//        currentTimeViewModel.setData(TimeFormat(hours,minutes,seconds))
//    }


    private fun registeredEvents() {
        binding.ukurBtn.setOnClickListener {
            listDataSensor.clear()
            var counter = -1
            ukurProgrss.visibility = View.VISIBLE
            var i = 0
            val reqHandler = Handler(Looper.getMainLooper())
            reqHandler.post(object : Runnable {
                override fun run() {
                    getSensorData()
                    ukurProgrss.progress = i
                    i+=10
                    counter++
                    if (counter >= 10) {
                        reqHandler.removeCallbacks(this)
                        val avgDataSensor = getAverageData(listDataSensor)
                        getUserCondition(avgDataSensor)
                        ukurProgrss.visibility = View.INVISIBLE
                    } else {
                        reqHandler.postDelayed(this, 1000)
                    }
                }
            })


        }
    }

    private fun sendNotification() {
        val listNaskes = naskesViewModel.getAllData()
        val userFcmToken = JSONArray()
        for(naskes in listNaskes){
            userFcmToken.put(naskes.token)
        }
        Log.d("request",userFcmToken.toString())
//        val userFcmToken: String =
//            "c7vcHgyPRLO-QVPFm88pI6:APA91bF0crp6b8r3GuNnUvGLbolsNBkkBWQBJH-qMtjyDtD6Mes_3IPsE9NZB_stAe-xAOKAy_fZ7opG69UG9l_6LvuqEyzsyZ7i0d3LUGyI8AQwB-qHSZEepXfDCGuTEbwqpL7NfoDU"
        val title: String = "Kondisi Darurat"
        val currentPasien = pasienViewModel.getData()!!
        val message: String =
            "Pasien ${currentPasien.firstName} ${currentPasien.lastName} dalam kondisi darurat"
        val postUrl: String = "https://fcm.googleapis.com/fcm/send"


        val requestQueue = Volley.newRequestQueue(activity);

        val mainObj: JSONObject = JSONObject()
        mainObj.put("registration_ids", userFcmToken);

        val notiObject = JSONObject();
        notiObject.put("content_avaiable", true);
        notiObject.put("title", title);
        notiObject.put("message", message)

        val dataObject = JSONObject()
        dataObject.put("content_avaiable", true);
        dataObject.put("title", title);
        dataObject.put("message", message)
        dataObject.put("uid", kondisiViewModel.getData()?.uid)

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
        Log.d("request",mainObj.toString())
        requestQueue.add(request);
    }

    private fun getUserCondition(avgDataSensor: dataSensor) {
        var respond = -1
        val queue = Volley.newRequestQueue(activity)
        val url = "https://covid-clasi.herokuapp.com/predict" // diganti ip address arduino
        val mainObj = JSONObject()
        mainObj.put("spo2",avgDataSensor.SpO2)
        mainObj.put("bpm",avgDataSensor.BPM)
        val request =JsonObjectRequest(Request.Method.POST, url, mainObj,
            { response ->
                respond = response.getInt("placement")
                if (respond == 1) sendNotification()
                val newKondisi = Kondisi(
                    kondisiViewModel.getData()?.id,
                    kondisiViewModel.getData()?.uid,
                    avgDataSensor.BPM.toInt(),
                    avgDataSensor.SpO2.toInt(),
                    respond,
                    null
                )
                kondisiViewModel.updateDatabase(newKondisi)
                Toast.makeText(activity, "Selesai", Toast.LENGTH_SHORT).show()
                updateText()
            },
            { error ->
                Log.d("respond",error.message.toString())
            })
        queue.add(request)
    }

    private fun getAverageData(listData: MutableList<dataSensor>): dataSensor {
        var totalBPM = 0f
        var totalSpO2 = 0f
        var counter = 0
        for (data in listData) {
            if (data.BPM == "" || data.SpO2 == "") {
                data.BPM = "0"
                data.SpO2 = "0"
                counter++
            }
            totalBPM += data.BPM.toFloat()
            totalSpO2 += data.SpO2.toFloat()
        }
        val avgBPM = totalBPM / (listData.size - counter)
        val avgSpO2 = totalSpO2 / (listData.size - counter)
        return dataSensor(avgBPM.roundToInt().toString(), avgSpO2.roundToInt().toString())
    }

    @SuppressLint("SetTextI18n")
    private fun getSensorData() {
        val queue = Volley.newRequestQueue(activity)
        val url = "http://192.168.43.254/data" // diganti ip address arduino
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                val data = Gson().fromJson(response.toString(), dataSensor::class.java)
                listDataSensor.add(data)
            },
            {
                Toast.makeText(activity,"Tidak terhubung dengan sensor",Toast.LENGTH_SHORT).show()
            })
        queue.add(stringRequest)
    }

//    private fun createORTSession( ortEnvironment: OrtEnvironment) : OrtSession {
//        val modelBytes = resources.openRawResource( R.raw.sklearn_model ).readBytes()
//        return ortEnvironment.createSession( modelBytes )
//    }
//    private fun runPrediction( bpm : Float ,spo2:Float, ortSession: OrtSession , ortEnvironment: OrtEnvironment ) : Long {
//        // Get the name of the input node
//        val inputName = ortSession.inputNames?.iterator()?.next()
//        // Make a FloatBuffer of the inputs
//        val floatBufferInputs = FloatBuffer.wrap( floatArrayOf( spo2,bpm ) )
//        // Create input tensor with floatBufferInputs of shape ( 1 , 1 )
//        val inputTensor = OnnxTensor.createTensor( ortEnvironment , floatBufferInputs , longArrayOf( 1, 2 ) )
//        // Run the model
//        val results = ortSession.run( mapOf( inputName to inputTensor ) )
//        // Fetch and return the results
//        Log.d("test",results.toString())
//        val output = results[0].value as Array<Long>
//        return output[0]
//    }
}