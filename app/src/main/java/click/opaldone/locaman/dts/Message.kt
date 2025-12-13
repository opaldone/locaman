package click.opaldone.locaman.dts

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import android.location.Location
import click.opaldone.locaman.loga.show_log

data class Message(
    val tp: String,
    val nik: String,
    val issender: Boolean = true,
    val roomid: String?,
    val content: String,
) {
    companion object {
        const val RLOCA    = "rloca"
        const val ALOCA    = "aloca"
        const val SENDERHI = "sender_hi"
        const val GOCHAT = "go_chat"
    }
}

data class Lo(
    val lat: Double,
    val lng: Double,
    val acc: Float
)

data class ClientHi(
    val bat: Int,
    val pos: Lo?
)

fun decMessage(msg: String): Message? {
    var ret: Message? = null

    val gson = Gson()
    ret = gson.fromJson(msg, Message::class.java)

    return ret
}

fun getJsonLoca(nikin: String?, locain: Location?, batin: Int): String {
    val gson = Gson()

    var lo_pos: Lo? = null;

    if (locain != null) {
        lo_pos = Lo(
            lat = locain.latitude,
            lng = locain.longitude,
            acc = locain.accuracy
        )
    }

    val ob = ClientHi(
        bat = batin,
        pos = lo_pos
    )

    val cojs = gson.toJson(ob);

    val msg = Message(
        tp = Message.ALOCA,
        nik = nikin ?: "",
        roomid = null,
        content = cojs
    )

    val ret = gson.toJson(msg)

    return ret
}

fun getJsonHi(nikin: String?): String {
    val gson = Gson()

    val msg = Message(
        tp = Message.SENDERHI,
        nik = nikin ?: "",
        roomid = null,
        content = ""
    )

    val ret = gson.toJson(msg)

    return ret
}
