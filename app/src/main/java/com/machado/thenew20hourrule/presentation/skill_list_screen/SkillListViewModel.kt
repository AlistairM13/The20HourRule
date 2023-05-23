package com.machado.thenew20hourrule.presentation.skill_list_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machado.thenew20hourrule.data.local.entities.Skill
import com.machado.thenew20hourrule.domain.repository.SkillRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class SkillListViewModel @Inject constructor(
    val repository: SkillRepository
) : ViewModel() {

    val allSkills = repository.getAllSkills()
    private var deletedSkill: Skill? = null
    fun insertSkill(skill: Skill) = viewModelScope.launch {
        repository.insertSkill(skill)
    }

    fun deleteSkill(skill: Skill) = viewModelScope.launch {
        deletedSkill = skill
        repository.deleteSkill(skill)
    }

    fun undoDelete() {
        deletedSkill?.let { insertSkill(it) }
        deletedSkill = null
    }

}