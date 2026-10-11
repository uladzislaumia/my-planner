package com.uladzislaumia.myplanner.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.getSystemService
import com.uladzislaumia.myplanner.domain.model.PlannerItem
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val alarmManager = context.getSystemService<AlarmManager>()

    fun scheduleTaskReminder(item: PlannerItem) {
        val dueDate = item.dueDate ?: return
        val reminderTime = dueDate.time

        // Don't schedule for past dates or completed items
        if (reminderTime <= System.currentTimeMillis() || item.isCompleted) {
            cancelTaskReminder(item.id)
            return
        }

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra(NotificationReceiver.EXTRA_TASK_ID, item.id)
            putExtra(NotificationReceiver.EXTRA_TASK_TITLE, item.title)
            putExtra(NotificationReceiver.EXTRA_TASK_DESCRIPTION, item.description)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            item.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager?.canScheduleExactAlarms() == false) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminderTime,
                    pendingIntent,
                )
            } else {
                alarmManager?.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminderTime,
                    pendingIntent,
                )
            }
            Timber.d("Scheduled alarm for taskId: ${item.id} at $reminderTime")
        } catch (e: SecurityException) {
            Timber.e(e, "SecurityException scheduling exact alarm for taskId: ${item.id}")
            alarmManager?.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminderTime,
                pendingIntent,
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to schedule alarm for taskId: ${item.id}")
        }
    }

    fun cancelTaskReminder(taskId: String) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
        )

        if (pendingIntent != null) {
            alarmManager?.cancel(pendingIntent)
            pendingIntent.cancel()
            Timber.d("Cancelled alarm for taskId: $taskId")
        }
    }
}
