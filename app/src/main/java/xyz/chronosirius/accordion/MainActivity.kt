package xyz.chronosirius.accordion

import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.preference.PreferenceManager
import xyz.chronosirius.accordion.directs.DirectMessageScreen
import xyz.chronosirius.accordion.servers.ServerScreen
import xyz.chronosirius.accordion.ui.theme.AccordionTheme
import xyz.chronosirius.accordion.viewmodels.RequestViewModel

// UI components file
// https://developer.android.com/develop/ui/compose/documentation

class MainActivity : ComponentActivity() {

    val requestViewModel by viewModels<RequestViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AccordionTheme {
                // Jetpack Compose basically uses lambdas inside lambdas to create UI components
                // which the Android system will render
                // We never need to update these components manually, the system will do it for us
                // when it detects a change in a value it triggers a recomposition
                // and redraws the screen with the new values/data
                val gatewayConnected by DiscordGatewayService.isGatewayConnected.collectAsStateWithLifecycle()
                val isRequesting by requestViewModel.isRequesting.collectAsStateWithLifecycle()
                val latestMessage by DiscordGatewayService.latestMessage.collectAsStateWithLifecycle()
                var startService = true
                val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
                for (t in am.getRunningServices(Integer.MAX_VALUE)) {
                    if (t.service.className == DiscordGatewayService::class.java.name) {
                        Log.d("MainActivity", "Service already running")
                        startService = false
                    }
                }
                if (startService) startForegroundService(Intent(this, DiscordGatewayService::class.java))
                var loggedIn by remember { mutableStateOf<Boolean>(true) }
                //loggedIn = PreferenceManager.getDefaultSharedPreferences(this).getString("token", null) != null
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    if (!loggedIn) {
                        LoginScreen(sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@MainActivity), context = this@MainActivity)
                        return@Scaffold
                    }
                    Scaffold(
                        modifier=Modifier.padding(innerPadding),
                        topBar= {
                            if (!gatewayConnected || isRequesting) {
                                LinearProgressIndicator(modifier=Modifier.fillMaxWidth())
                            }
                        },
                        bottomBar = {
                            var selectedItem by remember { mutableIntStateOf(0) }
                            val items = listOf("Home", "DMS", "Servers")
                            val selectedIcons = listOf(painterResource(R.drawable.home_filled), painterResource(R.drawable.chat_bubble_filled), painterResource(R.drawable.forum_filled))
                            val unselectedIcons =
                                listOf(painterResource(R.drawable.home_outlined), painterResource(R.drawable.chat_bubble_outlined), painterResource(R.drawable.forum_outlined))

                            NavigationBar {
                                items.forEachIndexed { index, item ->
                                    NavigationBarItem(
                                        icon = {
                                            Icon(
                                                (if (selectedItem == index) selectedIcons[index] else unselectedIcons[index]),
                                                contentDescription = item
                                            )
                                        },
                                        label = { Text(item) },
                                        selected = selectedItem == index,
                                        onClick = { selectedItem = index }
                                    )
                                }
                            }
                            /*
                            BottomAppBar(
                                modifier = Modifier.height(96.dp),
                                actions = {
                                    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth().padding(0.dp).fillMaxHeight(), verticalAlignment=Alignment.CenterVertically) {
                                        IconButton(onClick = {
                                            navController.navigate("home")
                                        }) {
                                            Icon(Icons.Outlined.Home, contentDescription = "Home", modifier = Modifier.size(32.dp).fillMaxHeight())
                                        }
                                        IconButton(modifier = Modifier.fillMaxHeight(), onClick = {
                                            // load dms pannel
                                            navController.navigate("dms")

                                        }) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center,
                                                modifier = Modifier.fillMaxHeight()
                                            ) {
                                                Icon(
                                                    painterResource(R.drawable.chat_bubble),
                                                    contentDescription = "DMS",
                                                )
                                                Text(text="DMs", modifier = Modifier.padding(top=4.dp))
                                            }
                                        }
                                        IconButton(onClick = {
                                            // load servers pannel
                                            navController.navigate("servers")
                                        }) {
                                            Icon(
                                                painterResource(R.drawable.forum),
                                                contentDescription = "Servers",
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }
                                }
                            )*/
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "home",
                            modifier = Modifier.padding(innerPadding),
                            enterTransition = { slideInVertically() },
                            exitTransition = { ExitTransition.None },
                        ) {
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
                            composable("servers") {
                                ServerScreen(navController)
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
        return super.onPause()
    }

    override fun onResume() {
        Log.d("MainActivity", "onResume")
        return super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}