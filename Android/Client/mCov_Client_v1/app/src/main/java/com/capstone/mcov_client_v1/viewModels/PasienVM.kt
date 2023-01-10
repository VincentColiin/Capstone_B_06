package com.capstone.mcov_client_v1.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.mcov_client_v1.models.Pasien
import com.google.firebase.firestore.FirebaseFirestore


class PasienVM : ViewModel() {
    private val firestoreDatabase: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mutablePasien = MutableLiveData<Pasien>()
    private val currentPasien: LiveData<Pasien> get() = mutablePasien

    fun setData(pasien: Pasien) {
        mutablePasien.value = pasien
    }

    fun getData(): Pasien? {
        return currentPasien.value
    }

    fun refreshData(currentUserUID: String) {
        firestoreDatabase.collection("pasiens")
            .whereEqualTo("uid", currentUserUID)
            .get()
            .addOnSuccessListener {
                setData(it.documents[0].toObject(Pasien::class.java)!!)
            }
    }

    fun updateData(currentUserUID:String, token: String){
        firestoreDatabase.collection("pasiens")
            .whereEqualTo("uid", currentUserUID)
            .get()
            .addOnSuccessListener {
                val updatePasien = it.documents[0].toObject(Pasien::class.java)!!
                updatePasien.token = token
                firestoreDatabase.collection("pasiens")
                    .document(it.documents[0].id)
                    .set(updatePasien)
            }
    }
}