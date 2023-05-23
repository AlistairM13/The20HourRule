package com.machado.thenew20hourrule.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Skill(
    val skillName: String,
    val timeSpent: Int,
    val finalGoal: String,
    @PrimaryKey(autoGenerate = true) val skillId: Long? = null
)
