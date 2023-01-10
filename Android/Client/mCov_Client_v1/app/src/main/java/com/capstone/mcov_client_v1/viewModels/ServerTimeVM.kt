package com.capstone.mcov_client_v1.viewModels

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.mcov_client_v1.models.ServerTime
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ServerTimeVM: ViewModel() {
    private val firestoreDatabase: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mutableServerTime = MutableLiveData<ServerTime>()
    val currentServerTime: LiveData<ServerTime> get() = mutableServerTime

    fun setData(condition: ServerTime) {
        mutableServerTime.value = condition
    }

    fun getData(): ServerTime? {
        return currentServerTime.value
    }

    fun updateData() {
        firestoreDatabase.collection("servertime")
            .document("CwNkkw8oetAJQCLkxwqB")
            .set(ServerTime("currentServerTime"))
            .addOnCompleteListener {
                firestoreDatabase.collection("servertime")
                    .whereEqualTo("id", "currentServerTime")
                    .get()
                    .addOnSuccessListener {
                        if (!it.isEmpty) {
                            setData(it.documents[0].toObject(ServerTime::class.java)!!)

                        }
                    }
            }
    }
}