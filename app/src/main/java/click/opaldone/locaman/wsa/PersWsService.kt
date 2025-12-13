package click.opaldone.locaman.wsa

import android.app.Service
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.IBinder
import android.os.BatteryManager;
import okhttp3.WebSocket
import okhttp3.OkHttpClient
import okhttp3.Request
import android.content.Intent
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import java.util.concurrent.TimeUnit
import java.util.UUID
import android.Manifest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import click.opaldone.locaman.R
import click.opaldone.locaman.dts.ShareTools
import click.opaldone.locaman.dts.Message
import click.opaldone.locaman.dts.getJsonHi
import click.opaldone.locaman.dts.decMessage
import click.opaldone.locaman.dts.getJsonLoca
import click.opaldone.locaman.loga.show_log

class PersWsService : Service() {
    private var wscli: WebSocket? = null
    private var wslistener: PersWsListener? = null
    private var wsnik: String? = null
    private var wsurl: String? = null

    private lateinit var okcli: OkHttpClient
    private lateinit var locli: FusedLocationProviderClient

    private val notificationId = 18798
    private val channelId = "locaman_service_channel"
    private val channelName = "locaman_chanel"

    private var need_reconnect: Boolean = true

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_SECHA = "ACTION_SECHA"
    }

    private fun createChannel() {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "The service for persistent ws connection"
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun buildNotification(msg: String): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_fr)
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_dr))
            .setContentTitle("Locaman notification")
            .setContentText(msg)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
    }

    fun notif(msg: String) {
        val bld = buildNotification(msg)

        val man = getSystemService(NotificationManager::class.java)
        man.notify(notificationId, bld)
    }

    private fun _send(txt: String) {
        wscli?.send(txt)
    }

    fun send_hi() {
        val js = getJsonHi(wsnik)
        _send(js)
    }

    fun send_loca() {
        if (ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            notif("Permissions were not permitted")
            return
        }

        locli.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token,
        ).addOnSuccessListener { loca ->
            val bm = getSystemService(BATTERY_SERVICE) as BatteryManager
            val bat = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

            val js = getJsonLoca(wsnik, loca, bat)

            _send(js)
        }
    }

    fun isAppInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun try_start_chat(msg: Message) {
        val pack = this.getString(R.string.chat_pack)
        val chat_act = this.getString(R.string.chat_act)

        if (!isAppInstalled(pack)) {
            notif("Opaloca is not installed")
            return
        }

        if (msg.roomid == null) {
            notif("Trying start chat roomid is empty")
            return
        }

        val launchIntent = packageManager.getLaunchIntentForPackage(pack)

        if (launchIntent == null) {
            notif("Opaloca launchIntent is null")
            return
        }

        launchIntent.putExtra("roomid", msg.roomid)

        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(launchIntent)

        val intent = Intent(chat_act).apply {
            setPackage(pack)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("roomid", msg.roomid)
        }
        sendBroadcast(intent)
    }

    fun ws_msg(text: String) {
        val msg = decMessage(text)

        msg?.let {
            when(msg.tp) {
                Message.RLOCA -> send_loca()
                Message.GOCHAT -> try_start_chat(msg)
            }
        }
    }

    private fun isConnected(): Boolean {
        return wslistener?.isConnected() ?: false
    }

    private fun startMe() {
        val notification = buildNotification("Started")
        startForeground(notificationId, notification)
    }

    fun scheduleReconnect() {
        if (!need_reconnect) {
            return
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (!isConnected()) {
                connectWebSocket()
            }
        }, 5000)
    }

    private fun connectWebSocket() {
        val sha = ShareTools(this)
        wsnik = sha.get_nik()
        wsurl = sha.get_ws_url()

        val request = Request.Builder()
            .url(wsurl!!)
            .build()

        wslistener = PersWsListener(this)
        wscli = okcli.newWebSocket(request, wslistener!!)
    }

    private fun settingsChanged() {
        val sha = ShareTools(this)
        val nik = sha.get_nik()
        val url = sha.get_ws_url()

        if (url != wsurl) {
            wscli?.close(1000, "Url was changed")
            return
        }

        if (nik != wsnik) {
            wsnik = nik
            send_hi()
        }
    }

    override fun onCreate() {
        super.onCreate()

        val sha = ShareTools(this)
        sha.set_ws_connected(false)

        okcli = OkHttpClient.Builder()
            .pingInterval(20, TimeUnit.SECONDS)
            .readTimeout(0, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        locli = LocationServices.getFusedLocationProviderClient(this)

        createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                startMe()
                connectWebSocket()
            }
            ACTION_STOP -> {
                stopSelf()
            }
            ACTION_SECHA -> {
                settingsChanged()
            }
            else -> {
                startMe()
                connectWebSocket()
            }
        }

        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        need_reconnect = false

        wscli?.close(1000, "Service destroyed")
        okcli.dispatcher.executorService.shutdown()

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
