package com.capstone.mcov_client_v1.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class ServerTime(
    val id: String?=null,
    @ServerTimestamp
    var timestamp: Date?=null
)
