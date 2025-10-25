package click.opaldone.locaman.dts

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import android.location.Location

data class Message(
    val tp: String,
    val content: String
) {
    companion object {
        const val RLOCA    = "rloca"
        const val ALOCA    = "aloca"
        const val SENDERHI = "sender_hi"
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

fun decMessage(msg: String): Message? {
    var ret: Message? = null

    try {
        val gson = Gson()
        ret = gson.fromJson(msg, Message::class.java)
    } catch(e: JsonSyntaxException) {
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
