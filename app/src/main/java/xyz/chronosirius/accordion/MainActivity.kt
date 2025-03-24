package xyz.chronosirius.accordion

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.asStateFlow
import xyz.chronosirius.accordion.ui.theme.AccordionTheme
import xyz.chronosirius.accordion.viewmodels.AccordionViewModel

// UI components file
// https://developer.android.com/develop/ui/compose/documentation

class MainActivity : ComponentActivity() {

    //val requestViewModel by viewModels<RequestViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var startService = true
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (t in am.getRunningServices(Integer.MAX_VALUE)) {
            if (t.service.className == DiscordGatewayService::class.java.name) {
                Log.d("MainActivity", "Service already running")
                startService = false
            }
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        if (startService) startForegroundService(Intent(this, DiscordGatewayService::class.java))



        setContent {
            AccordionTheme {
                val navController = rememberNavController()
                val currentBackStack by navController.currentBackStackEntryAsState();
                val currentDestination = currentBackStack?.destination?.route
                Log.d("MainActivity", "Current destination: $currentDestination")

                val isNotConnected = currentBackStack?.sharedViewModel<AccordionViewModel>(navController)?.isRequesting?.collectAsState()
                Log.d("MainActivity", "isNotConnected: ${isNotConnected?.value}")
                Scaffold(
                    topBar = @Composable {
                        if (isNotConnected?.value == true) {
                            Log.d("MainActivity", "Showing progress bar")
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        } else {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    },
                    bottomBar = @Composable {
                        NavigationBar(
                            modifier=Modifier.fillMaxWidth(),
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary
                        ) {
                            NavigationBarItem(
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                icon = {
                                    if (currentBackStack?.destination?.parent?.route == "servers")
                                        Icon(Icons.Filled.Home, contentDescription = "Servers")
                                    else
                                        Icon(Icons.Outlined.Home, contentDescription = "Servers")
                                },
                                label = { Text("Servers") },
                                selected = currentBackStack?.destination?.parent?.route == "servers",
                                onClick = {
                                    navController.navigate("servers") {
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                            NavigationBarItem(
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                icon = {
                                    if (currentBackStack?.destination?.parent?.route == "DMS")
                                        Icon(painterResource(R.drawable.forum_filled), contentDescription = "DMS")
                                    else
                                        Icon(painterResource(R.drawable.forum_outlined), contentDescription = "DMS")
                                },
                                label = { Text("DMS") },
                                selected = currentBackStack?.destination?.parent?.route == "DMS",
                                onClick = {
                                    Log.d("MainActivity", "Navigating to DMS")
                                    navController.navigate("DMS") {
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                            NavigationBarItem(
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                icon = {
                                    if (currentBackStack?.destination?.parent?.route == "profile") {
                                        Icon(Icons.Filled.Face, contentDescription = "Profile")
                                    } else {
                                        Icon(Icons.Outlined.Face, contentDescription = "Profile")
                                    }
                                },
                                label = { Text("Profile") },
                                selected = currentBackStack?.destination?.parent?.route == "profile",
                                onClick = {
                                    navController.navigate("profile") {
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    MainNavPoint(navController, this as Context, modifier=Modifier.padding(it).fillMaxSize())
                }
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
        //wakeLock.release()
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}

@Composable
fun ChannelScreen(navController: NavController, serverId: Int, channelId: Int) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Home")
    }
}