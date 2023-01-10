package com.capstone.mcov_client_v1.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.mcov_client_v1.models.Kondisi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class KondisiVM : ViewModel() {
    private val firestoreDatabase: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mutableKondisi = MutableLiveData<Kondisi>()
    val currentKondisi: LiveData<Kondisi> get() = mutableKondisi

    fun setData(id: String?, kondisi: Kondisi) {
        kondisi.id = id ?: mutableKondisi.value?.id
        mutableKondisi.value = kondisi
    }

    fun getData(): Kondisi? {
        return currentKondisi.value
    }

    fun updateData(currentUserUID: String) {
        firestoreDatabase.collection("sensors")
            .whereEqualTo("uid", currentUserUID)
            .get()
            .addOnCompleteListener {
                it.addOnSuccessListener { doc ->
                    setData(doc.documents[0].id, doc.documents[0].toObject(Kondisi::class.java)!!)
                }
            }
//            .addOnSuccessListener {
//                setData(it.documents[0].id,it.documents[0].toObject(Kondisi::class.java)!!)
//                Log.d("kondisi",it.documents[0].toObject(Kondisi::class.java).toString())
//            }
    }

    fun updateDatabase(kondisi: Kondisi) {
        firestoreDatabase.collection("sensors")
            .document(mutableKondisi.value?.id!!)
            .set(kondisi)
            .addOnCompleteListener {
                firestoreDatabase.collection("sensors")
                    .whereEqualTo("uid", kondisi.uid)
                    .get()
                    .addOnSuccessListener {
                        setData(it.documents[0].id, it.documents[0].toObject(Kondisi::class.java)!!)
                    }
            }
    }

}