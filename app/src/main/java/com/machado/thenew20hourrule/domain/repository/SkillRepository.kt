package com.machado.thenew20hourrule.domain.repository

import androidx.lifecycle.LiveData
import com.machado.thenew20hourrule.data.local.entities.Session
import com.machado.thenew20hourrule.data.local.entities.Skill
import com.machado.thenew20hourrule.data.local.relations.SkillWithSessions

interface SkillRepository {

    suspend fun insertSkill(skill: Skill)

    suspend fun updateSkill(skill: Skill)

    suspend fun deleteSkill(skill: Skill)

    fun getAllSkills(): LiveData<List<Skill>>

    suspend fun insertSession(session: Session)

    suspend fun getSkillWithSessions(skillId: Long): SkillWithSessions

}