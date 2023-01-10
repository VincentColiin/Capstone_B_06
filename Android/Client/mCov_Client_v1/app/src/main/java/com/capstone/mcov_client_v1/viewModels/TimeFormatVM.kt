package com.capstone.mcov_client_v1.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.mcov_client_v1.models.Pasien
import com.capstone.mcov_client_v1.models.TimeFormat
import com.google.firebase.firestore.FirebaseFirestore

class TimeFormatVM:ViewModel() {
    private val mutableTime = MutableLiveData<TimeFormat>()
    private val currentTime: LiveData<TimeFormat> get() = mutableTime

    fun setData(time: TimeFormat) {
        mutableTime.value = time
    }

    fun getData(): TimeFormat? {
        return currentTime.value
    }

}