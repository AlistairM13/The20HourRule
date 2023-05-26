package com.machado.thenew20hourrule.presentation.skill_list_screen

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machado.thenew20hourrule.data.local.entities.Skill
import com.machado.thenew20hourrule.domain.repository.SkillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SkillListViewModel @Inject constructor(
    val state: SavedStateHandle,
    val repository: SkillRepository,
    val application: Application
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

    var skill: Skill? = null

    init {
        skill = state.get<Skill>(SKILL_STATE) // Check if savedStateHandle has the skill stored

        if (skill == null) { // Or else check if the sharedPreferences has it stored
            val sharedPref =
                application.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE)

            if (sharedPref.getString(SHARED_PREF_SKILL_NAME, "") != "") {
                val skillFromSharedPref = Skill(
                    skillName = sharedPref.getString(SHARED_PREF_SKILL_NAME, "")!!,
                    timeSpent = sharedPref.getFloat(SHARED_PREF_SKILL_TIME_SPENT, 0f).toDouble(),
                    finalGoal = sharedPref.getString(SHARED_PREF_SKILL_GOAL, "")!!,
                    skillId = sharedPref.getLong(SHARED_PREF_SKILL_ID, 0)
                )
                skill = skillFromSharedPref
            } else {
                Log.i("MYTAG", "empty shared pref")
            }
        }
    }

    fun onSkillItemClicked(selectedSkill: Skill) {
        val sharedPref = application.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE)
        sharedPref.edit().apply {
            putLong(SHARED_PREF_SKILL_ID, selectedSkill.skillId!!)
            putString(SHARED_PREF_SKILL_NAME, selectedSkill.skillName)
            putFloat(SHARED_PREF_SKILL_TIME_SPENT, selectedSkill.timeSpent.toFloat())
            putString(SHARED_PREF_SKILL_GOAL, selectedSkill.finalGoal)
            apply()
        }
        state.set(SKILL_STATE, selectedSkill)
    }

    companion object {
        const val SKILL_STATE = "SKILL_STATE"
        const val SHARED_PREF_SKILL_ID = "SHARED_PREF_SKILL_ID"
        const val SHARED_PREF_SKILL_NAME = "SHARED_PREF_SKILL_NAME"
        const val SHARED_PREF_SKILL_TIME_SPENT = "SHARED_PREF_SKILL_TIME_SPENT"
        const val SHARED_PREF_SKILL_GOAL = "SHARED_PREF_SKILL_GOAL"
        const val SHARED_PREFERENCE = "SHARE_PREFERENCE"
    }
}