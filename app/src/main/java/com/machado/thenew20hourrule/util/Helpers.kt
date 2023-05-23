package com.machado.thenew20hourrule.util

object Helpers {
    fun getFormattedTime(time: Long): String {
        val hours = time % 86400 / 3600
        val minutes = time % 86400 % 3600 / 60
        val seconds = time % 86400 % 3600 % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}