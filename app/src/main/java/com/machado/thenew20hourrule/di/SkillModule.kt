package com.machado.thenew20hourrule.di

import android.app.Application
import androidx.room.Room
import com.machado.thenew20hourrule.data.local.SkillDatabase
import com.machado.thenew20hourrule.data.repositoryImpl.SkillRepositoryImpl
import com.machado.thenew20hourrule.domain.repository.SkillRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SkillModule {

    @Provides
    @Singleton
    fun provideSkillRepository(db: SkillDatabase): SkillRepository {
        return SkillRepositoryImpl(db.dao)
    }

    @Provides
    @Singleton
    fun provideSkillDatabase(app: Application): SkillDatabase {
        return Room.databaseBuilder(
            app,
            SkillDatabase::class.java,
            "skill_db"
        ).build()
    }

}