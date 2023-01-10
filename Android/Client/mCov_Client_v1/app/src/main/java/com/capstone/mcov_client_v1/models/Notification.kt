package com.capstone.mcov_client_v1.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.*


data class Notification(
    var id:String?=null,
    var uid: String?=null,
    var title: String?=null,
    var message: String?=null,
    @ServerTimestamp
    var time: Date?=null
)