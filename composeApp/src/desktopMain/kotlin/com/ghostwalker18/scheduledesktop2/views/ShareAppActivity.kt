package com.ghostwalker18.scheduledesktop2.views

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import getNavigator
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.*
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.scan_qr_code
import scheduledesktop2.composeapp.generated.resources.share_app

/**
 * Эта функция представляет собой экран приложения для возможности поделиться им.
 * @author Ипатов Никита
 * @since 1.0
 */
@Preview
@Composable
fun ShareAppActivity() {
    val navigator = getNavigator()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.share_app)) },
                navigationIcon = {
                    IconButton(
                        { navigator.goBack()}
                    ){
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "")
                    }
                },
            )
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
        ){
            Column(
                modifier = Modifier.weight(0.5f)
            ) {
                Text(
                    text = stringResource(Res.string.scan_qr_code),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth()
                )
                Image(painterResource(Res.drawable.qr_code), null)
            }
            Text(
                text = stringResource(Res.string.or),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp)
            )
            Button({},
                modifier = Modifier
                    .weight(0.5f)
            ){
                Text(
                    text = stringResource(Res.string.share_link),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}