package click.opaldone.locaman

import android.app.Application
import android.os.Handler
import click.opaldone.locaman.wsa.WsWorker

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()

        WsWorker.schedule(this, 0)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)

        WsWorker.schedule(this, 10)
    }
}
