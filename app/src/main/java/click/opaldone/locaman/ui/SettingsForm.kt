package click.opaldone.locaman.ui

import android.os.Build
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.em
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import kotlin.system.exitProcess
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.foundation.background
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.res.stringArrayResource
import android.content.Intent
import click.opaldone.locaman.dts.ShareTools
import click.opaldone.locaman.wsa.PersWsService
import click.opaldone.locaman.R
import click.opaldone.locaman.wsa.WsWorker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpaList(state: MutableState<String>, list_items: Array<String>, lbl: String) {
    var expa by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expa,
        onExpandedChange = { expa = !expa },
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                .fillMaxWidth()
                .padding(5.dp),
            shape = RoundedCornerShape(7.dp),
            value = state.value,
            onValueChange = {
                state.value = it
                expa = true
            },
            label = {
                Text(
                    text = lbl,
                    fontSize = 12.sp,
                    color = Color(0xff777777),
                    style = TextStyle(letterSpacing = 0.3.em)
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expa) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                unfocusedBorderColor = Color(0xfff0f0f0),
                focusedBorderColor = Color(0xff2c5de5)
            )
        )
        DropdownMenu(
            modifier = Modifier
            .background(Color.White)
            .exposedDropdownSize(true),
            properties = PopupProperties(focusable = false),
            expanded = expa,
            onDismissRequest = { expa = false },
        ) {
            list_items.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = {
                        state.value = opt
                        expa = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

fun saveSettings(ctx: Context, urlin: String, nikin: String) {
    val sha = ShareTools(ctx)
    sha.set_host_url(urlin)
    sha.set_nik(nikin)
}

@Composable
fun ShowSettingsForm(ctx: Context) {
    val sha = ShareTools(ctx)
    var host_url: MutableState<String> = remember { mutableStateOf(sha.get_host_url()) }
    var wsnik by remember{ mutableStateOf(sha.get_nik()) }
    val host_list = stringArrayResource(R.array.host_list)
    val activity = (LocalContext.current as? Activity)

    Column() {
        Text(
            text = ctx.getString(R.string.head_set),
            color = Color(0xffffffff),
            fontSize = 24.sp,
            modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xff2c5de5))
            .padding(
                start = 30.dp,
                end = 30.dp,
                top = 15.dp,
                bottom = 15.dp
            )
        )

        Column(
            Modifier.fillMaxSize().padding(
                start = 30.dp,
                end = 30.dp,
                top = 15.dp,
                bottom = 15.dp

            )
        ) {
            ExpaList(host_url, host_list, "HOST")

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                shape = RoundedCornerShape(7.dp),
                value = wsnik,
                onValueChange = {
                    wsnik = it
                },
                label = {
                    Text(
                        text = "NICKNAME",
                        fontSize = 12.sp,
                        color = Color(0xff777777),
                        style = TextStyle(letterSpacing = 0.3.em)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xffe0e0e0),
                    focusedBorderColor = Color(0xff2c5de5)
                )
            )

            Spacer(Modifier.padding(15.dp))

            Row {
                Button(
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color(0xffffffff),
                        containerColor = Color(0xff2c5de5)
                    ),
                    shape = RoundedCornerShape(7.dp),
                    onClick = {
                        saveSettings(ctx, host_url.value, wsnik)

                        val intent = Intent(ctx, PersWsService::class.java).apply {
                            action = PersWsService.ACTION_SECHA
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            ctx.startForegroundService(intent)
                        } else {
                            ctx.startService(intent)
                        }
                    }
                ) {
                    Text(
                        text = ctx.getString(R.string.apply),
                        modifier = Modifier
                        .padding(13.dp),
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(7.dp))

                Button(
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color(0xffffffff),
                        containerColor = Color(0xff2ce55d)
                    ),
                    shape = RoundedCornerShape(7.dp),
                    onClick = {
                        activity?.finishAndRemoveTask();
                    }
                ) {
                    Text(
                        text = ctx.getString(R.string.close),
                        modifier = Modifier
                        .padding(13.dp),
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
