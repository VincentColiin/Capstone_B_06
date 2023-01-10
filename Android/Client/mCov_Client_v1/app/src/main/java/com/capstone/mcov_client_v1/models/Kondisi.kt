package com.capstone.mcov_client_v1.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Kondisi(
    var id: String?=null,
    var uid: String?=null,
    var bpm : Int=0,
    var spo2: Int=0,
    var kondisi: Int=-1,
    @ServerTimestamp
    var lastChecked: Date?=null
)