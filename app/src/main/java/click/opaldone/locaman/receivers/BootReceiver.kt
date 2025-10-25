package click.opaldone.locaman.receivers

import android.content.BroadcastReceiver
import android.os.Build
import android.content.Context
import android.content.Intent
import click.opaldone.locaman.wsa.WsWorker
import click.opaldone.locaman.loga.show_log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        // if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        show_log("Locaman BootReceiver.onReceive")
    }
}
