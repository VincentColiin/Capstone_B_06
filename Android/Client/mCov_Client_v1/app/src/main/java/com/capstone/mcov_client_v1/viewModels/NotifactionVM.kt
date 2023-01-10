package com.capstone.mcov_client_v1.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.mcov_client_v1.models.Notification
import com.google.firebase.firestore.FirebaseFirestore

class NotifactionVM : ViewModel() {
    private val firestoreDatabase: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val notifList = mutableListOf<Notification>()
    private val mutableNotif = MutableLiveData<List<Notification>>()
    private val currentSensor: LiveData<List<Notification>> get() = mutableNotif

    fun setData(id:String,newNotif: Notification) {
        newNotif.id = id
        notifList.add(newNotif)
        mutableNotif.value = notifList
    }

    fun clearData(){
        notifList.clear()
    }

    fun getAllData(): List<Notification> {
        return mutableNotif.value ?: listOf<Notification>()
    }

    fun getData(position: Int): Notification? {
        return currentSensor.value?.get(position)
    }

    fun updateData(currentUserUID: String) {
        firestoreDatabase.collection("notifications")
            .whereEqualTo("uid", currentUserUID)
            .get()
            .addOnSuccessListener {
                clearData()
                for (document in it.documents){
                    setData(document.id,document.toObject(Notification::class.java)!!)
                }
            }
    }

    fun removeData(idDoc:String,idUser:String){
        firestoreDatabase.collection("notifications")
            .document(idDoc)
            .delete()
            .addOnCompleteListener {
                updateData(idUser)
            }
    }
}