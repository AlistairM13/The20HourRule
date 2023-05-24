package com.machado.thenew20hourrule.util

object TimeHelper {
    const val ONE_SECOND_IN_MILLIS: Long = 1_000
    const val ONE_MINUTE_IN_SECONDS: Long = 60
    const val ONE_HOUR_IN_MINUTES: Long = 60

    fun getFormattedTime(time: Long): String {
        val hours = time % 86400 / 3600
        val minutes = time % 86400 % 3600 / 60
        val seconds = time % 86400 % 3600 % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}