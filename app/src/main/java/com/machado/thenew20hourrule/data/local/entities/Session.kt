package com.machado.thenew20hourrule.data.local.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

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
@Parcelize
data class Session(
    val objective: String,
    val sessionDurationInMin: Double,
    val skillId: Long,
    val sessionDateTime: String? = null,
    @PrimaryKey(autoGenerate = true) val sessionId: Long? = null
) : Parcelable