package click.opaldone.locaman.wsa

import android.os.Build
import android.content.Context
import androidx.work.CoroutineWorker
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import androidx.work.WorkerParameters
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkRequest
import androidx.work.WorkManager
import androidx.core.app.NotificationCompat
import java.util.concurrent.TimeUnit
import android.content.Intent
import android.app.ActivityManager;
import click.opaldone.locaman.R
import click.opaldone.locaman.dts.ShareTools
import click.opaldone.locaman.loga.show_log

class WsWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    companion object {
        const val W_NAME = "click_locaman_ws_worker"
        const val CHANNEL_ID = "locaman_worker_channel"
        const val NOTIFICATION_ID = 18798
        const val CHANNEL_NAME = "locaman_worker_chanel"

        fun schedule(context: Context, de: Long) {
            val first = OneTimeWorkRequestBuilder<WsWorker>()

            if (de > 0) {
                first.setInitialDelay(de, TimeUnit.MINUTES)
            }

            val fi = first.build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                W_NAME,
                ExistingWorkPolicy.REPLACE,
                fi
            )
        }
    }

    private fun isServiceRunning(): Boolean {
        val sha = ShareTools(applicationContext)

        val is_ws_con = sha.is_ws_connected()

        val manager = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val ex_ser = manager.getRunningServices(Integer.MAX_VALUE)
            .any { it.service.className == PersWsService::class.java.name }


        return is_ws_con && ex_ser
    }

    private fun startLocaService() {
        val intent = Intent(applicationContext, PersWsService::class.java).apply {
            action = PersWsService.ACTION_START
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            applicationContext.startForegroundService(intent)
        } else {
            applicationContext.startService(intent)
        }
    }

    private fun scheduleNext() {
        val next = OneTimeWorkRequestBuilder<WsWorker>()
            .setInitialDelay(5, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            W_NAME,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            next
        )
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            if (!isServiceRunning()) {
                startLocaService()
            }

            scheduleNext()

            Result.success()
        }
    }
}
