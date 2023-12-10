/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.myapplication.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text
import com.example.myapplication.presentation.theme.MyApplicationTheme
import com.example.myapplication.presentation.websocket.WebSocketClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class MainActivity : ComponentActivity() {
    private lateinit var client: OkHttpClient

    private val request: Request = Request.Builder().url("ws://10.0.2.2:8000").build()
    private val listener: WebSocketListener = WebSocketClient()
    private var ws: WebSocket? = null

    companion object {
        lateinit var context: Context
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = applicationContext
        client = OkHttpClient()
        setContent {
            WearApp(
                onStartClick = {
                    start()
                },
                onStopClick = {
                    stop()
                }
            )
        }
    }

    private fun start() {
        GlobalScope.launch(Dispatchers.IO) {
            // 创建 WebSocket 连接
            ws = client.newWebSocket(request, listener)
        }
    }

    private fun stop() {
        GlobalScope.launch(Dispatchers.IO) {
            // Check if the WebSocket connection is not null before attempting to close it
            ws?.close(1000, "User initiated disconnect")
        }
    }
}

@Composable
fun WearApp(onStartClick: () -> Unit, onStopClick: () -> Unit) {
    MyApplicationTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            var isStarted by remember { mutableStateOf(false) }
            Button(
                onClick = {
                    if (isStarted) {
                        onStopClick()
                    } else {
                        onStartClick()
                    }
                    isStarted = !isStarted
                },
                modifier = Modifier.size(ButtonDefaults.LargeButtonSize)
            ) {
                Text(text = if (isStarted) "Stop" else "Start")
            }
        }
    }
}