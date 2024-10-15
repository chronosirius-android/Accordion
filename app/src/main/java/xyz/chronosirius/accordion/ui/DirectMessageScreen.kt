package xyz.chronosirius.accordion.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import xyz.chronosirius.accordion.viewmodels.RequestViewModel
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import xyz.chronosirius.accordion.data.DataArray

@Composable
fun DirectMessageScreen(vm: RequestViewModel, navController: NavController) {
    var directMessages by remember { mutableStateOf(DataArray.empty()) }
    LaunchedEffect(true) {
        // This will fetch the direct messages from the server
        // and update the UI with the messages list
        // will load a conversation screen fragment with the messages once loaded
        // vm.getArray { url("https://discord.com/api/v9/users/@me/channels") }
    }
    Column {

    }
}