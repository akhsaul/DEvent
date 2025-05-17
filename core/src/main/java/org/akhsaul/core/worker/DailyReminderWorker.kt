package org.akhsaul.core.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import org.akhsaul.core.R
import org.akhsaul.core.domain.repository.EventRepository
import org.akhsaul.core.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@HiltWorker
class DailyReminderWorker @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    //private val apiService: ApiService,
    private val eventRepository: EventRepository
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        return when(val result = eventRepository.getNotificationEvent()){
            is org.akhsaul.core.data.Result.Error -> {
                Result.failure(buildDataFailure(result.message))
            }
            is org.akhsaul.core.data.Result.Success -> {
                val event = result.data
                if (event == null) {
                    Result.failure(buildDataFailure("No Data"))
                } else {
                    val formatter =
                        DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())
                    val formattedDate = event.beginTime.toLocalDateTime().format(formatter)
                    showNotification(
                        event.name,
                        "Recommendation event for you on $formattedDate"
                    )
                    Result.success(buildData { putString("event_name", event.name) })
                }
            }
            org.akhsaul.core.data.Result.Loading -> {
                Result.failure(buildDataFailure("Worker doesn't need Loading State"))
            }
        }

//        return try {
//            val apiResult = apiService.getNotificationEvent()
//            if (apiResult.isSuccessful) {
//                val events = apiResult.body()?.listEvents
//                if (!events.isNullOrEmpty()) {
//                    val event = events.first()
//                    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())
//                    val formattedDate = event.beginTime.toLocalDateTime().format(formatter)
//                    showNotification(event.name, "Recommendation event for you on $formattedDate")
//                    Result.success(buildData { putString("event_name", event.name) })
//                } else {
//                    Result.failure(buildDataFailure("No Data"))
//                }
//            } else {
//                Result.failure(buildDataFailure(apiResult.message()))
//            }
//        } catch (t: Throwable) {
//            Result.failure(buildDataFailure(t.message))
//        }
    }

    private fun buildData(action: Data.Builder.() -> Unit): Data {
        return Data.Builder()
            .apply(action)
            .build()
    }

    private fun buildDataFailure(message: String?) = buildData {
        putString("message", message ?: "Unknown Error")
    }

    private fun showNotification(title: String, description: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_24dp)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setChannelId(CHANNEL_ID)
        val channel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_daily_reminder"
        const val CHANNEL_NAME = "Daily Reminder Channel"
    }
}