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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
            textStyle = TextStyle(
                fontSize = 18.sp,
            ),
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
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expa) },
        )
        DropdownMenu(
            modifier = Modifier
                .background(Color(0xff373737))
                .exposedDropdownSize(true),
            properties = PopupProperties(focusable = false),
            shape = RoundedCornerShape(7.dp),
            expanded = expa,
            onDismissRequest = { expa = false },
        ) {
            list_items.forEach { opt ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = opt,
                            fontSize = 18.sp,
                            color = Color(0xffffffff)
                        )
                    },
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

@Composable
fun VersionInfo() {
    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

    val versionName = packageInfo.versionName

    Text(
        text = "Version $versionName",
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xff777777),
        fontSize = 14.sp,
        textAlign = TextAlign.Center
    )
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
    var wsnik = remember{ mutableStateOf(sha.get_nik()) }
    val host_list = stringArrayResource(R.array.host_list)
    val activity = (ctx as? Activity)

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
            ExpaList(host_url, host_list, "Host")

            Spacer(Modifier.padding(10.dp))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                textStyle = TextStyle(
                    fontSize = 18.sp
                ),
                shape = RoundedCornerShape(7.dp),
                value = wsnik.value,
                onValueChange = {
                    wsnik.value = it
                },
                label = {
                    Text(
                        text = "Map nickname",
                        fontSize = 12.sp
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            wsnik.value = ""
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = "Clear"
                        )
                    }
                }
            )

            Spacer(Modifier.padding(20.dp))

            Row {
                Button(
                    modifier = Modifier.weight(2f),
                    shape = RoundedCornerShape(7.dp),
                    onClick = {
                        saveSettings(ctx, host_url.value, wsnik.value)

                        val intent = Intent(ctx, PersWsService::class.java).apply {
                            action = PersWsService.ACTION_SECHA
                        }

                        ctx.startForegroundService(intent)

                        activity?.finishAndRemoveTask();
                    }
                ) {
                    Text(
                        text = ctx.getString(R.string.apply),
                        modifier = Modifier
                        .padding(7.dp),
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(7.dp))

                Button(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(7.dp),
                    onClick = {
                        activity?.finishAndRemoveTask();
                    }
                ) {
                    Text(
                        text = ctx.getString(R.string.close),
                        modifier = Modifier
                        .padding(7.dp),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(Modifier.padding(20.dp))

            VersionInfo()
        }
    }
}
