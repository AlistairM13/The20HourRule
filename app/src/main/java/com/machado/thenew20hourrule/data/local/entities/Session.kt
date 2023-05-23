package com.machado.thenew20hourrule.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Session(
    val objective: String,
    val sessionDateTime:String,
    val skillId:Long,
    @PrimaryKey(autoGenerate = true) val sessionId: Long? = null
)