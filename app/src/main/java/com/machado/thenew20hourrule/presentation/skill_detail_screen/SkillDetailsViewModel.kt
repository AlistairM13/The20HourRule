package com.machado.thenew20hourrule.presentation.skill_detail_screen

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machado.thenew20hourrule.data.local.entities.Session
import com.machado.thenew20hourrule.data.local.entities.Skill
import com.machado.thenew20hourrule.domain.repository.SkillRepository
import com.machado.thenew20hourrule.util.TimeHelper.ONE_HOUR_IN_MINUTES
import com.machado.thenew20hourrule.util.TimeHelper.ONE_MINUTE_IN_SECONDS
import com.machado.thenew20hourrule.util.TimeHelper.ONE_SECOND_IN_MILLIS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.DateFormat
import javax.inject.Inject

@HiltViewModel
class SkillDetailsViewModel @Inject constructor(
    val repository: SkillRepository,
    val state: SavedStateHandle
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
        state.set<Session>("session", session)
    }

    init {
        _session.value = state.get<Session>("session")
        state.get<Long>("currentTimeMillis")?.let {
            val startedTime = it
            val currentTime = System.currentTimeMillis()
            val differenceInTimeInMinutes =
                ((currentTime - startedTime).toDouble().div(ONE_SECOND_IN_MILLIS)
                    .div(ONE_MINUTE_IN_SECONDS))
            _session.value?.sessionDurationInMin?.let { maxDuration ->
                if (differenceInTimeInMinutes > maxDuration) {
                    _isSessionStarted.value = false
                    _isSessionFinished.value = true
                    _currentTimeInSeconds.value = maxDuration.toLong() * ONE_MINUTE_IN_SECONDS
                } else {
                    _isSessionStarted.value = true
                    _isSessionFinished.value = false
                    _currentTimeInSeconds.value =
                        (differenceInTimeInMinutes * ONE_MINUTE_IN_SECONDS).toLong()
                    val remainingDurationInSeconds =
                        (maxDuration - differenceInTimeInMinutes).times(ONE_MINUTE_IN_SECONDS)
                            .times(ONE_SECOND_IN_MILLIS).toLong()
                    startTimer(remainingDurationInSeconds)
                }
            }

        }
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
        state.set<Long>("currentTimeMillis", System.currentTimeMillis())
        var durationInMs =
            (_session.value!!.sessionDurationInMin * ONE_MINUTE_IN_SECONDS * ONE_SECOND_IN_MILLIS).toLong()

        if (_currentTimeInSeconds.value != 0L) {
            durationInMs -= (_currentTimeInSeconds.value!! * ONE_SECOND_IN_MILLIS)
        }

        startTimer(durationInMs)

    }

    private fun startTimer(durationInMs: Long) {
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
        val timeSpentInMin = _currentTimeInSeconds.value!!.toDouble()
            .div(ONE_MINUTE_IN_SECONDS)
        val currentDateTime = DateFormat.getDateTimeInstance().format(System.currentTimeMillis())

        repository.insertSession(
            session.value!!.copy(
                sessionDurationInMin = timeSpentInMin,
                sessionDateTime = currentDateTime
            )
        )
        repository.updateSkill(
            skill.copy(
                timeSpent = skill.timeSpent + timeSpentInMin.div(ONE_HOUR_IN_MINUTES)
            )
        )
    }

}