package com.capstone.mcov_naskes_v1.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Kondisi(
    var id: String?=null,
    var uid: String?=null,
    var bpm : Float=0f,
    var spo2: Float=0f,
    var kondisi: Int=-1,
    @ServerTimestamp
    var lastChecked: Date?=null
)
