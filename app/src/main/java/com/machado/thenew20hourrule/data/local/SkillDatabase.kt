package com.machado.thenew20hourrule.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.machado.thenew20hourrule.data.local.entities.Session
import com.machado.thenew20hourrule.data.local.entities.Skill

@Database(
    entities = [Skill::class, Session::class],
    version = 1
)
abstract class SkillDatabase : RoomDatabase() {
    abstract val dao:SkillDao
}