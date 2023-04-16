package com.example.util.simpletimetracker.core.extension

import android.content.BroadcastReceiver
import android.os.StrictMode
import java.util.Calendar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

inline fun <T, R> T.allowDiskWrite(block: T.() -> R): R {
    val oldPolicy = StrictMode.allowThreadDiskWrites()
    try {
        return block()
    } finally {
        StrictMode.setThreadPolicy(oldPolicy)
    }
}

fun Calendar.setWeekToFirstDay() {
    val another = Calendar.getInstance()
    another.timeInMillis = timeInMillis

    val currentTime = another.timeInMillis
    // Setting DAY_OF_WEEK have a weird behaviour so as if after that another field is set -
    // it would reset to current day. Use another calendar to manipulate day of week and get its time.
    another.set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
    // If went to future - go back a week
    if (another.timeInMillis > currentTime) {
        another.add(Calendar.DATE, -7)
    }

    timeInMillis = another.timeInMillis
}

fun Calendar.setToStartOfDay() {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

@OptIn(DelicateCoroutinesApi::class)
fun BroadcastReceiver.goAsync(
    coroutineScope: CoroutineScope = GlobalScope,
    finally: () -> Unit,
    block: suspend () -> Unit,
) {
    val result = goAsync()
    coroutineScope.launch {
        try {
            block()
        } finally {
            finally()
            // Always call finish(), even if the coroutineScope was cancelled
            result.finish()
        }
    }
}