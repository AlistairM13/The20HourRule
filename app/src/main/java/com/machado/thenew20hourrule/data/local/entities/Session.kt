package com.machado.thenew20hourrule.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey

import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            Skill::class,
            ["skillId"],
            ["skillId"],
            ForeignKey.CASCADE
        )
    ]
)
data class Session(
    val objective: String,
    val sessionDuration: Double,
    val skillId: Long,
    val sessionDateTime: String? = null,
    @PrimaryKey(autoGenerate = true) val sessionId: Long? = null
)