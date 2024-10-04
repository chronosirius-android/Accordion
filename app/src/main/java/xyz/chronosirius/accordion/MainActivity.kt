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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Observer
import xyz.chronosirius.accordion.ui.theme.AccordionTheme

// UI components file
// https://developer.android.com/develop/ui/compose/documentation

class MainActivity : ComponentActivity() {
    var gatewayConnected = mutableStateOf(false)
    private val intentBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("MainActivity", "Received broadcast " + intent?.action)
            if (intent?.action == "xyz.chronosirius.accordion.DISCORD_GATEWAY_CONNECTED") {
                gatewayConnected.value = true
            } else if (intent?.action == "xyz.chronosirius.accordion.DISCORD_GATEWAY_ERROR") {
                gatewayConnected.value = false
            }
        }
    }
    private var gatewayObserver: Observer<Boolean>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        registerReceiver(intentBroadcastReceiver, IntentFilter("xyz.chronosirius.accordion.DISCORD_GATEWAY_ERROR"),
            RECEIVER_NOT_EXPORTED
        )
        gatewayObserver = Observer { connected ->
            Log.d("MainActivity", "Gateway connected: $connected")
            this@MainActivity.gatewayConnected.value = connected
            Log.d("MainActivity", "Gateway connected1: $gatewayConnected")
        }
        gatewayObserver?.let {
            DiscordGatewayService.isGatewayConnected.observe(this, it)
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
                    val mGatewayConnected by remember { gatewayConnected }
                    if (mGatewayConnected) {
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
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(intentBroadcastReceiver)
        gatewayObserver?.let {
            DiscordGatewayService.isGatewayConnected.removeObserver(it)
        }
    }
}