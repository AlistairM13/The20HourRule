package com.machado.thenew20hourrule.presentation.history_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machado.thenew20hourrule.data.local.entities.Skill
import com.machado.thenew20hourrule.data.local.relations.SkillWithSessions
import com.machado.thenew20hourrule.domain.repository.SkillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    val repository: SkillRepository
) : ViewModel() {

    private val _allSessions: MutableLiveData<SkillWithSessions?> = MutableLiveData(null)
    val allSessions: LiveData<SkillWithSessions?> get() = _allSessions

    fun getAllSessions(skill: Skill) = viewModelScope.launch {
        val sessions = repository.getSkillWithSessions(skill.skillId!!)
        _allSessions.postValue(sessions)
    }

    fun orderByDuration(isReversed: Boolean) {
        _allSessions.value?.let { skillWithSessions ->
            var orderedByDuration = skillWithSessions.sessions.sortedBy { it.sessionDurationInMin }
            if (isReversed) orderedByDuration = orderedByDuration.reversed()
            _allSessions.postValue(
                skillWithSessions.copy(
                    sessions = orderedByDuration
                )
            )
        }
    }

    fun orderByDate(isReversed: Boolean) {
        _allSessions.value?.let { skillWithSessions ->
            var orderedByDate = skillWithSessions.sessions.sortedBy { it.createdOnMillis }
            if (isReversed) orderedByDate = orderedByDate.reversed()
            _allSessions.postValue(
                skillWithSessions.copy(
                    sessions = orderedByDate
                )
            )
        }
    }
}