package com.machado.thenew20hourrule.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.machado.thenew20hourrule.data.local.entities.Session
import com.machado.thenew20hourrule.data.local.entities.Skill

data class SkillWithSessions(
    @Embedded val skill: Skill,
    @Relation(
        parentColumn = "skillId",
        entityColumn = "skillId"
    )
    val sessions: List<Session>
)