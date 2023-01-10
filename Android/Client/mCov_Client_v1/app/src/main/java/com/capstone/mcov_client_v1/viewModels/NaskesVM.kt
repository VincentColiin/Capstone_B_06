package com.capstone.mcov_client_v1.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.mcov_client_v1.models.Notification
import com.capstone.mcov_client_v1.models.Pasien
import com.google.firebase.firestore.FirebaseFirestore

class NaskesVM:ViewModel() {
    private val firestoreDatabase: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val naskesList = mutableListOf<Pasien>()
    private val mutableNaskes = MutableLiveData<List<Pasien>>()
    private val currentSensor: LiveData<List<Pasien>> get() = mutableNaskes

    fun setData(newNotif: Pasien) {
        naskesList.add(newNotif)
        mutableNaskes.value = naskesList
    }

    fun clearData(){
        naskesList.clear()
    }

    fun getAllData(): List<Pasien> {
        return mutableNaskes.value ?: listOf<Pasien>()
    }

    fun getData(position: Int): Pasien? {
        return currentSensor.value?.get(position)
    }

    fun updateData() {
        firestoreDatabase.collection("naskes")
            .get()
            .addOnSuccessListener {
                clearData()
                for (document in it.documents){
                    setData(document.toObject(Pasien::class.java)!!)
                }
            }
    }
}