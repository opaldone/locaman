package click.opaldone.locaman.dts

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import android.location.Location
import click.opaldone.locaman.loga.show_log

data class Message(
    val tp: String,
    val content: String
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
    val cid: String,
    val nik: String,
    val bat: Int,
    val issender: Boolean = true,
    val pos: Lo?
)

data class ReqChat(
    val roomid: String
)

fun decMessage(msg: String): Message? {
    var ret: Message? = null

    val gson = Gson()
    ret = gson.fromJson(msg, Message::class.java)

    return ret
}

fun decReqChat(cont: String): ReqChat? {
    var ret: ReqChat? = null

    try {
        val gson = Gson()
        ret = gson.fromJson(cont, ReqChat::class.java)
    } catch(e: JsonSyntaxException) {
        show_log("decReqChat error: ${e.message}")
    }

    return ret
}

fun getJsonLoca(cidin: String, nikin: String?, locain: Location?, batin: Int): String {
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
        cid = cidin,
        nik = nikin ?: "",
        bat = batin,
        pos = lo_pos
    )

    val cojs = gson.toJson(ob);

    val msg = Message(
        tp = Message.ALOCA,
        content = cojs
    )

    val ret = gson.toJson(msg)

    return ret
}

fun getJsonHi(cidin: String, nikin: String?): String {
    val gson = Gson()

    val ob = ClientHi(
        cid = cidin,
        nik = nikin ?: "",
        bat = -1,
        pos = null
    )
    val cojs = gson.toJson(ob);

    val msg = Message(
        tp = Message.SENDERHI,
        content = cojs
    )

    val ret = gson.toJson(msg)

    return ret
}
