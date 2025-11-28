package click.opaldone.locaman.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.ActivityManager;
import click.opaldone.locaman.wsa.PersWsService
import click.opaldone.locaman.wsa.AlarmMan
import click.opaldone.locaman.dts.ShareTools
import click.opaldone.locaman.loga.show_log

class AlarmReceiver : BroadcastReceiver() {
    private fun isServiceRunning(ctx: Context): Boolean {
        val sha = ShareTools(ctx)

        val is_ws_con = sha.is_ws_connected()

        val manager = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val list = manager.getRunningServices(Integer.MAX_VALUE)

        val ex_ser = manager.getRunningServices(Integer.MAX_VALUE)
            .any { it.service.className == PersWsService::class.java.name }

        return is_ws_con && ex_ser
    }

    private fun startLocaService(ctx: Context) {
        val inte = Intent(ctx, PersWsService::class.java).apply {
            action = PersWsService.ACTION_START
        }

        ctx.startForegroundService(inte)
    }

    override fun onReceive(ctx: Context, intent: Intent) {
        if (!isServiceRunning(ctx)) {
            startLocaService(ctx)
        }

        AlarmMan.schedule(ctx, true)
    }
}
