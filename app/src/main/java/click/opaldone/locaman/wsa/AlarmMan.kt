package click.opaldone.locaman.wsa

import android.content.Context
import android.app.AlarmManager
import android.content.Intent
import android.app.PendingIntent
import click.opaldone.locaman.receivers.AlarmReceiver
import click.opaldone.locaman.loga.show_log

class AlarmMan {
    companion object {
        const val ALA_CODE: Int = 6856;
        const val ALA_INTERVAL: Long = 5 * 60 * 1000;

        fun schedule(ctx: Context, delay: Boolean) {
            val alam: AlarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            var tm = System.currentTimeMillis()
            if (delay) {
                tm += ALA_INTERVAL
            }

            val pinte = PendingIntent.getBroadcast(
                ctx,
                ALA_CODE,
                Intent(ctx, AlarmReceiver::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            alam.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                tm,
                pinte
            )
        }
    }
}
