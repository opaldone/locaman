package click.opaldone.locaman.dts

import android.content.Context
import androidx.core.content.edit
import click.opaldone.locaman.R

class ShareTools(
    private val ctx: Context
) {
    companion object {
        const val PREFS = "locaman_prefs"
        const val KURL = "host_url"
        const val KNIK = "nik"
        const val KCON = "is_ws_con"
    }

    private val sha = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun get_host_url(): String {
        return sha.getString(KURL, ctx.getString(R.string.host_url))!!
    }

    fun get_ws_url(cid: String): String {
        val _url = get_host_url()
        val po = ctx.getString(R.string.ws_port)
        return "wss://$_url:$po/ws/$cid/1"
    }

    fun get_nik(): String {
        return sha.getString(KNIK, ctx.getString(R.string.nik))!!
    }

    fun is_ws_connected(): Boolean {
        return sha.getBoolean(KCON, false)
    }

    fun set_ws_connected(vain: Boolean) {
        sha.edit {
            putBoolean(KCON, vain)
        }
    }

    fun set_host_url(nu: String) {
        sha.edit {
            putString(KURL, nu)
        }
    }

    fun set_nik(ni: String) {
        sha.edit {
            putString(KNIK, ni)
        }
    }
}
