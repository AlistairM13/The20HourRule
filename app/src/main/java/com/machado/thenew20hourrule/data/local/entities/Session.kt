package com.machado.thenew20hourrule.data.local.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.DateFormat

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
    val sessionDurationInMin: Float,
    val skillId: Long,
    val createdOnMillis: Long? = null,
    @PrimaryKey(autoGenerate = true) val sessionId: Long? = null
) : Parcelable {
    val createdDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(createdOnMillis)
}