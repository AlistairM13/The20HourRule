package com.machado.thenew20hourrule.data.local.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Skill(
    val skillName: String,
    val timeSpent: Double,
    val finalGoal: String,
    @PrimaryKey(autoGenerate = true) val skillId: Long? = null
) : Parcelable
