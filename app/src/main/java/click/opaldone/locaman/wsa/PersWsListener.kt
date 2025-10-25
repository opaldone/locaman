package click.opaldone.locaman.wsa

import okhttp3.WebSocketListener
import okhttp3.WebSocket
import okhttp3.Response
import click.opaldone.locaman.dts.ShareTools

class PersWsListener(
    private val ser: PersWsService,
) : WebSocketListener() {
    private var _isconnected: Boolean = false

    fun isConnected(): Boolean {
        return _isconnected
    }

    private fun set_sha_con(valin: Boolean) {
        _isconnected = valin
        val sha = ShareTools(ser)
        sha.set_ws_connected(valin)
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        set_sha_con(true)
        ser.notif("Connected")
        ser.send_hi()
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        set_sha_con(false)
        ser.notif("Closing: ${reason}")
        webSocket.close(1000, null)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        set_sha_con(false)
        ser.notif("Closed: ${reason}")
        ser.scheduleReconnect()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        set_sha_con(false)
        ser.scheduleReconnect()
        ser.notif("Failure: ${t.message}")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        ser.ws_msg(text)
    }
}
