package com.machado.thenew20hourrule.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.machado.thenew20hourrule.data.local.entities.Session
import com.machado.thenew20hourrule.data.local.entities.Skill
import com.machado.thenew20hourrule.data.local.relations.SkillWithSessions

@Dao
interface SkillDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkill(skill: Skill)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: Session)

    @Transaction
    @Query("SELECT * FROM skill WHERE skillId=:skillId")
    suspend fun getSkillWithSessions(skillId: Long): List<SkillWithSessions>

}