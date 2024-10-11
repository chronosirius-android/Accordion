package xyz.chronosirius.accordion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Observer
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.preference.PreferenceManager
import xyz.chronosirius.accordion.data.DataObject
import xyz.chronosirius.accordion.ui.DirectMessageScreen
import xyz.chronosirius.accordion.ui.LoginScreen
import xyz.chronosirius.accordion.ui.theme.AccordionTheme

// UI components file
// https://developer.android.com/develop/ui/compose/documentation

class MainActivity : ComponentActivity() {

    var gatewayConnected by mutableStateOf(false)
    val gwObserver = Observer<Boolean> {
        gatewayConnected = it
    }

    var latestMessage by mutableStateOf(DataObject.empty())
    val messageObserver = Observer<DataObject> {
        Log.d("MainActivity", "Message received: ${it.toPrettyString()}")
        latestMessage = it
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        messageObserver.let { DiscordGatewayService.latestMessage.observe(this, it) }
        gwObserver.let { DiscordGatewayService.isGatewayConnected.observe(this, it) }
        setContent {
            AccordionTheme {
                // Jetpack Compose basically uses lambdas inside lambdas to create UI components
                // which the Android system will render
                // We never need to update these components manually, the system will do it for us
                // when it detects a change in a value it triggers a recomposition
                // and redraws the screen with the new values/data
                startForegroundService(Intent(this, DiscordGatewayService::class.java))
                var loggedIn by remember { mutableStateOf<Boolean>(true) }
                var loading by remember { mutableStateOf<Boolean>(true) }

                if (gatewayConnected) loading = false
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    if (!loggedIn) {
                        LoginScreen(sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@MainActivity), context = this@MainActivity)
                        return@Scaffold
                    }
                    Scaffold(
                        modifier=Modifier.padding(innerPadding),
                        topBar= {
                            if (loading) {
                                LinearProgressIndicator(modifier=Modifier.fillMaxWidth())
                            }
                        },
                        bottomBar = {
                            BottomAppBar(
                                actions = {
                                    IconButton(onClick = { navController.navigate("home") }) {
                                        Icon(Icons.Outlined.Home, contentDescription = "Home")
                                    }
                                    IconButton(onClick = { navController.navigate("dms") }) {
                                        Icon(Icons.Outlined.Send, contentDescription = "Home")
                                    } 
                                }
                            )
                        }
                    ) { innerPadding ->
                        NavHost(navController, startDestination = "home", modifier = Modifier.padding(innerPadding)) {
                            composable("home") {
                                val k = rememberScrollState()
                                Column(modifier = Modifier.verticalScroll(k)) {
                                    Text("Home screen")
                                    Text("Message: ${latestMessage.toPrettyString()}")
                                }
                            }
                            composable("dms") {
                                DirectMessageScreen(navController)
                            }
                        }
                    }
                }
                /*
                    Log.d("MainActivity", "Recomposing UI")
                    //val mGatewayConnected by remember { gatewayConnected }
                    var scroll = rememberScrollState()
                    Column(modifier = Modifier.verticalScroll(scroll)) {
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
                            modifier = Modifier.padding(top=16.dp)
                        )
                    }
                }*/
            }
        }
    }

    override fun onPause() {
        Log.d("MainActivity", "onPause")
        gwObserver.let { DiscordGatewayService.isGatewayConnected.removeObserver(it) }
        messageObserver.let { DiscordGatewayService.latestMessage.removeObserver(it) }
        return super.onPause()
    }

    override fun onResume() {
        Log.d("MainActivity", "onResume")
        messageObserver.let { DiscordGatewayService.latestMessage.observe(this, it) }
        gwObserver.let { DiscordGatewayService.isGatewayConnected.observe(this, it) }
        return super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}