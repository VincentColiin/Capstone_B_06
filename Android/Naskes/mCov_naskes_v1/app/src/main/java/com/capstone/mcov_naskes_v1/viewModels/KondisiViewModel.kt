package com.capstone.mcov_naskes_v1.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.mcov_naskes_v1.models.Kondisi
import com.capstone.mcov_naskes_v1.models.User
import com.google.firebase.firestore.FirebaseFirestore

class KondisiViewModel:ViewModel() {
    private val firestoreDatabase: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val listKondisi = mutableListOf<Kondisi>()
    private val mutablePasien = MutableLiveData<List<Kondisi>>()
    private val currentPasien: LiveData<List<Kondisi>> get() = mutablePasien

    fun setData(id:String,newNotif: Kondisi) {
        newNotif.id = id
        listKondisi.add(newNotif)
        mutablePasien.value = listKondisi
    }

    private fun clearData(){
        listKondisi.clear()
    }

    fun getAllData(): List<Kondisi> {
        return currentPasien.value ?: listOf<Kondisi>()
    }

    fun getData(uid:String): Kondisi? {
        return currentPasien.value?.find {
            it.uid == uid
        }
    }

    fun refreshData() {
        firestoreDatabase.collection("sensors")
            .get()
            .addOnSuccessListener {
                clearData()
                for (document in it.documents) {
                    setData(document.id, document.toObject(Kondisi::class.java)!!)
                }
            }
    }

    fun updateData(kondisi: Kondisi){
        firestoreDatabase.collection("sensors")
            .document(kondisi.id!!)
            .set(kondisi)
            .addOnCompleteListener {
                refreshData()
            }
    }
}