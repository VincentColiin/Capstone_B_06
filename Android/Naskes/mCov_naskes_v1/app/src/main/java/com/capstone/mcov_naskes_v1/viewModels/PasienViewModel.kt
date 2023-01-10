package com.capstone.mcov_naskes_v1.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.mcov_naskes_v1.models.User
import com.google.firebase.firestore.FirebaseFirestore

class PasienViewModel:ViewModel() {
    private val firestoreDatabase: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val listPasien = mutableListOf<User>()
    private val mutablePasien = MutableLiveData<List<User>>()
    private val currentPasien: LiveData<List<User>> get() = mutablePasien

    fun setData(id:String,newNotif: User) {
        newNotif.id = id
        listPasien.add(newNotif)
        mutablePasien.value = listPasien
    }

    private fun clearData(){
        listPasien.clear()
    }

    fun getAllData(): List<User> {
        Log.d("pasien",currentPasien.value.toString())
        return currentPasien.value ?: listOf<User>()
    }

    fun getDataPosition(position: Int): User? {
        return currentPasien.value?.get(position)
    }

    fun getDataUID(uid: String): User?{
        for(pasien in currentPasien.value!!){
            if(pasien.uid == uid) return pasien
        }
        return null
    }

    fun refreshData() {
        firestoreDatabase.collection("pasiens")
            .get()
            .addOnSuccessListener {
                clearData()
                for (document in it.documents){
                    setData(document.id,document.toObject(User::class.java)!!)
                }
            }
    }
}