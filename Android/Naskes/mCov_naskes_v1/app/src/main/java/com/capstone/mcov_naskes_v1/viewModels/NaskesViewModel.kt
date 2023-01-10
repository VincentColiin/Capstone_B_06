package com.capstone.mcov_naskes_v1.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.mcov_naskes_v1.models.User
import com.google.firebase.firestore.FirebaseFirestore

class NaskesViewModel:ViewModel() {
    private val firestoreDatabase: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mutableNaskes = MutableLiveData<User>()
    private val currentNaskes: LiveData<User> get() = mutableNaskes

    fun setData(pasien: User) {
        mutableNaskes.value = pasien
    }

    fun getData(): User? {
        return currentNaskes.value
    }

    fun refreshData(currentUserUID: String) {
        firestoreDatabase.collection("naskes")
            .whereEqualTo("uid", currentUserUID)
            .get()
            .addOnSuccessListener {
                setData(it.documents[0].toObject(User::class.java)!!)
            }
    }

    fun updateData(currentUserUID:String, token: String){
        firestoreDatabase.collection("naskes")
            .whereEqualTo("uid", currentUserUID)
            .get()
            .addOnSuccessListener {
                val updateNaskes = it.documents[0].toObject(User::class.java)!!
                updateNaskes.token = token
                firestoreDatabase.collection("naskes")
                    .document(it.documents[0].id)
                    .set(updateNaskes)
            }
    }
}