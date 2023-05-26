package com.machado.thenew20hourrule.data.repositoryImpl

import androidx.lifecycle.LiveData
import com.machado.thenew20hourrule.data.local.SkillDao
import com.machado.thenew20hourrule.data.local.entities.Session
import com.machado.thenew20hourrule.data.local.entities.Skill
import com.machado.thenew20hourrule.data.local.relations.SkillWithSessions
import com.machado.thenew20hourrule.domain.repository.SkillRepository

class SkillRepositoryImpl(
    private val dao: SkillDao
) : SkillRepository {
    override suspend fun insertSkill(skill: Skill) = dao.insertSkill(skill)

    override suspend fun updateSkill(skill: Skill) = dao.updateSkill(skill)

    override suspend fun deleteSkill(skill: Skill) = dao.deleteSkill(skill)

    override fun getAllSkills(): LiveData<List<Skill>> = dao.getAllSkills()

    override suspend fun insertSession(session: Session) = dao.insertSession(session)

    override suspend fun getSkillWithSessions(skillId: Long): SkillWithSessions =
        dao.getSkillWithSessions(skillId)
}