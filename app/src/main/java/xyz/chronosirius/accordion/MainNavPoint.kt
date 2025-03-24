package xyz.chronosirius.accordion

import android.content.Context
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import xyz.chronosirius.accordion.directs.ConversationScreen
import xyz.chronosirius.accordion.directs.DirectMessageScreen
import xyz.chronosirius.accordion.servers.ServerScreen

@Composable
fun MainNavPoint(navController: NavHostController, context: Context, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "DMS",
        modifier = modifier,
        exitTransition = {
            ExitTransition.None
        }
    ) {
        navigation(
            route = "DMS",
            startDestination = "conversation_list",
            enterTransition = {
                EnterTransition.None
            }
        ) {
            composable("conversation_list") {
                DirectMessageScreen(navController, it.sharedViewModel(navController), context)
            }

            composable("channels/{channelId}",
                enterTransition = { slideInHorizontally() }
            )
            { backStackEntry ->
                val channelId = backStackEntry.arguments?.getString("channelId")?.toLong()
                if (channelId != null) {
                    ConversationScreen(
                        navController,
                        channelId,
                        backStackEntry.sharedViewModel(navController)
                    )
                }
            }
        }

        navigation(
            route = "servers",
            startDestination = "server_list",
            enterTransition = { EnterTransition.None }
        ) {
            composable("server_list") {
                ServerScreen(navController)
            }

            navigation(
                route = "server/{serverId}",
                startDestination = "channel_list"
            ) {
                composable(
                    "channel_list",
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None }
                ) {
                    ServerScreen(navController)
                }
                composable("channels/{channelId}")
                { backStackEntry ->
                    val serverId = backStackEntry.arguments?.getString("serverId")?.toInt()
                    val channelId = backStackEntry.arguments?.getString("channelId")?.toInt()
                    ChannelScreen(navController, serverId!!, channelId!!)
                }
            }
        }

        navigation(
            route = "profile",
            startDestination = "my_profile"
        ) {
            composable("my_profile") {
                Text("Profile")
            }
        }
    }
}

