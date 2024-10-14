package xyz.chronosirius.accordion.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import xyz.chronosirius.accordion.viewmodels.RequestViewModel

@Composable
fun ConversationScreen(channelId: Int, vm: RequestViewModel, navController: NavController) {
    LaunchedEffect(true) {
        // This will fetch the direct messages from the server
        // and update the UI with the messages list
        // will load a conversation screen fragment with the messages once loaded
        // vm.get { url("https://discord.com/api/v9/users/@me/channels") }
    }
    Column {

    }
}