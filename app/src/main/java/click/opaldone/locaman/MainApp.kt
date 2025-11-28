package click.opaldone.locaman

import android.app.Application
import click.opaldone.locaman.wsa.AlarmMan
import click.opaldone.locaman.loga.show_log

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()

        AlarmMan.schedule(this, false)
    }
}
