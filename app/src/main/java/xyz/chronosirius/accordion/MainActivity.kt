package xyz.chronosirius.accordion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import okhttp3.internal.immutableListOf
import xyz.chronosirius.accordion.data.DataObject
import xyz.chronosirius.accordion.ui.theme.AccordionTheme

// UI components file
// https://developer.android.com/develop/ui/compose/documentation

class MainActivity : ComponentActivity() {
    private var gatewayConnected = mutableStateOf(false)
    private var gatewayObserver: Observer<Boolean>? = null
    private var messageObserver: Observer<DataObject>? = null
    private var messageList = mutableStateOf(DataObject.empty())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        gatewayObserver = Observer { connected ->
            Log.d("MainActivity", "Gateway connected: $connected")
            this@MainActivity.gatewayConnected.value = connected
            Log.d("MainActivity", "Gateway connected1: $gatewayConnected")
        }
        gatewayObserver?.let {
            DiscordGatewayService.isGatewayConnected.observe(this, it)
        }
        messageObserver = Observer { message ->
            Log.d("MainActivity", "Message received: $message")
            messageList.value = message
            Log.d("MainActivity", "Message list: $messageList")
        }
        messageObserver?.let {
            DiscordGatewayService.latestMessage.observe(this, it)
        }

        setContent {
            AccordionTheme {
                // Jetpack Compose basically uses lambdas inside lambdas to create UI components
                // which the Android system will render
                // We never need to update these components manually, the system will do it for us
                // when it detects a change in a value it triggers a recomposition
                // and redraws the screen with the new values/data
                startService(Intent(this, DiscordGatewayService::class.java))
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Log.d("MainActivity", "Recomposing UI")
                    //val mGatewayConnected by remember { gatewayConnected }
                    if (gatewayConnected.value) {
                        Log.d("MainActivity", "Gateway connected")
                        Text(
                            text = "Connected to Discord Gateway",
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        Text(
                            text = "Connecting to Discord Gateway...",
                            modifier = Modifier.padding(innerPadding)
                        )
                    }

                    Text(
                        text = "Message: ${messageList.value.toPrettyString()}",
                        modifier = Modifier.padding(innerPadding).padding(top=16.dp)
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gatewayObserver?.let {
            DiscordGatewayService.isGatewayConnected.removeObserver(it)
        }
    }
}