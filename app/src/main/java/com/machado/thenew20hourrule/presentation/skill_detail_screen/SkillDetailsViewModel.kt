package com.machado.thenew20hourrule.presentation.skill_detail_screen

import android.app.Application
import android.content.Context
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machado.thenew20hourrule.data.local.entities.Session
import com.machado.thenew20hourrule.data.local.entities.Skill
import com.machado.thenew20hourrule.domain.repository.SkillRepository
import com.machado.thenew20hourrule.presentation.skill_list_screen.SkillListViewModel
import com.machado.thenew20hourrule.util.TimeHelper.ONE_HOUR_IN_MINUTES
import com.machado.thenew20hourrule.util.TimeHelper.ONE_MINUTE_IN_SECONDS
import com.machado.thenew20hourrule.util.TimeHelper.ONE_SECOND_IN_MILLIS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SkillDetailsViewModel @Inject constructor(
    val repository: SkillRepository,
    val state: SavedStateHandle,
    val application: Application
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
        state.set<Session>(SESSION_STATE, session)

        val sharedPref = application.getSharedPreferences(
            SkillListViewModel.SHARED_PREFERENCE,
            Context.MODE_PRIVATE
        ).edit()

        sharedPref.apply {
            putFloat(SHARED_PREF_DURATION, session.sessionDurationInMin)
            putLong(SHARED_PREF_SESSION_ID, session.sessionId ?: 0)
            putString(SHARED_PREF_OBJECTIVE, session.objective)
            putLong(SHARED_PREF_SKILL_ID, session.skillId)
            apply()
        }
    }


    init {
        _session.value = state.get<Session>(SESSION_STATE)
        state.get<Long>(CURRENT_TIME_STATE)?.let {
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
        if (_session.value == null) {
            val sharedPref = application.getSharedPreferences(
                SkillListViewModel.SHARED_PREFERENCE,
                Context.MODE_PRIVATE
            )
            if (sharedPref.getLong(SHARED_PREF_SESSION_ID, -1L) != -1L) {
                val sessionFromSharedPref = Session(
                    objective = sharedPref.getString(SHARED_PREF_OBJECTIVE, "") ?: "",
                    sessionDurationInMin = sharedPref.getFloat(SHARED_PREF_DURATION, 0f),
                    skillId = sharedPref.getLong(SHARED_PREF_SKILL_ID, -1L),
                    sessionId = sharedPref.getLong(SHARED_PREF_SESSION_ID, -1L),
                )
                _session.value = sessionFromSharedPref
            }
        }
        if (state.get<Long>(CURRENT_TIME_STATE) == null) {
            val sharedPref = application.getSharedPreferences(
                SkillListViewModel.SHARED_PREFERENCE,
                Context.MODE_PRIVATE
            )
            val sharedPrefCurrentTime = sharedPref.getLong(SHARED_PREF_CURRENT_TIME, -1L)
            if (sharedPrefCurrentTime != -1L) {
                val currentTime = System.currentTimeMillis()
                val differenceInTimeInMinutes =
                    ((currentTime - sharedPrefCurrentTime).toDouble().div(ONE_SECOND_IN_MILLIS)
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
                        val remainingTimeInSeconds =
                            (maxDuration - differenceInTimeInMinutes).times(ONE_MINUTE_IN_SECONDS)
                                .times(ONE_SECOND_IN_MILLIS).toLong()
                        startTimer(remainingTimeInSeconds)
                    }
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
        val currentTimeInMillis = System.currentTimeMillis()
        state.set<Long>(CURRENT_TIME_STATE, currentTimeInMillis)
        application.getSharedPreferences(SkillListViewModel.SHARED_PREFERENCE, Context.MODE_PRIVATE)
            .edit().apply {
                putLong(SHARED_PREF_CURRENT_TIME, currentTimeInMillis)
                apply()
            }

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
        val timeSpentInMin = _currentTimeInSeconds.value!!.toFloat()
            .div(ONE_MINUTE_IN_SECONDS)

        repository.insertSession(
            session.value!!.copy(
                sessionDurationInMin = timeSpentInMin,
                createdOnMillis = System.currentTimeMillis()
            )
        )
        repository.updateSkill(
            skill.copy(
                timeSpent = skill.timeSpent + timeSpentInMin.div(ONE_HOUR_IN_MINUTES)
            )
        )
    }

    companion object {
        private const val SESSION_STATE = "SESSION_STATE"
        private const val CURRENT_TIME_STATE = "CURRENT_TIME_STATE"
        private const val SHARED_PREF_DURATION = "SHARED_PREF_DURATION"
        private const val SHARED_PREF_SESSION_ID = "SHARED_PREF_SESSION_ID"
        private const val SHARED_PREF_SKILL_ID = "SHARED_PREF_SKILL_ID"
        private const val SHARED_PREF_OBJECTIVE = "SHARED_PREF_OBJECTIVE"
        private const val SHARED_PREF_CURRENT_TIME = "SHARED_PREF_CURRENT_TIME"
    }
}