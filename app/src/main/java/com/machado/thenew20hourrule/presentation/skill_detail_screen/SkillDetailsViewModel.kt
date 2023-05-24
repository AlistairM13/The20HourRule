package com.machado.thenew20hourrule.presentation.skill_detail_screen

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machado.thenew20hourrule.data.local.entities.Session
import com.machado.thenew20hourrule.data.local.entities.Skill
import com.machado.thenew20hourrule.domain.repository.SkillRepository
import com.machado.thenew20hourrule.util.TimeHelper.ONE_HOUR_IN_MINUTES
import com.machado.thenew20hourrule.util.TimeHelper.ONE_MINUTE_IN_SECONDS
import com.machado.thenew20hourrule.util.TimeHelper.ONE_SECOND_IN_MILLIS
import kotlinx.coroutines.launch
import javax.inject.Inject

class SkillDetailsViewModel @Inject constructor(
    val repository: SkillRepository
) : ViewModel() {

    private var _session: MutableLiveData<Session?> = MutableLiveData(null)
    val session: LiveData<Session?> get() = _session

    private var _isSessionStarted: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSessionStarted: LiveData<Boolean> get() = _isSessionStarted

    private var _isSessionFinished: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSessionFinished: LiveData<Boolean> get() = _isSessionFinished

    private var countDownTimer: CountDownTimer? = null

    private var _currentTimeInSeconds: MutableLiveData<Long> = MutableLiveData(0)
    val currentTimeInSeconds: LiveData<Long> get() = _currentTimeInSeconds

    fun setupSession(session: Session) {
        _session.value = session
    }

    fun startOrStopSession() {
        if (isSessionStarted.value == true) {
            stopSession()
        } else {
            startSession()
        }
    }

    private fun startSession() {
        _isSessionStarted.value = true
        var durationInMs =
            (_session.value!!.sessionDuration * ONE_MINUTE_IN_SECONDS * ONE_SECOND_IN_MILLIS).toLong()

        if (_currentTimeInSeconds.value != 0L) {
            durationInMs -= (_currentTimeInSeconds.value!! * ONE_SECOND_IN_MILLIS)
        }

        countDownTimer = object : CountDownTimer(durationInMs, ONE_SECOND_IN_MILLIS) {
            override fun onTick(millisUntilFinished: Long) {
                _currentTimeInSeconds.value = _currentTimeInSeconds.value?.plus(1)
            }

            override fun onFinish() {
                _isSessionStarted.value = false
                _isSessionFinished.value = true
            }
        }.start()

    }

    private fun stopSession() {
        countDownTimer?.cancel()
        _isSessionStarted.value = false
    }

    fun resetSession() {
        countDownTimer?.cancel()
        _isSessionStarted.value = false
        _session.value = null
        _currentTimeInSeconds.value = 0L
        _isSessionFinished.value = false
    }

    fun updateSession(skill: Skill) = viewModelScope.launch {
        val timeSpentInHours = _currentTimeInSeconds.value!!.toDouble()
            .div(ONE_MINUTE_IN_SECONDS)
            .div(ONE_HOUR_IN_MINUTES)

        repository.insertSession(session.value!!.copy(
            sessionDuration = timeSpentInHours
        ))
        repository.updateSkill(
            skill.copy(
                timeSpent = skill.timeSpent + timeSpentInHours
            )
        )
    }

}