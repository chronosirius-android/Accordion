package xyz.chronosirius.accordion.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import xyz.chronosirius.accordion.data.DataArray
import xyz.chronosirius.accordion.viewmodels.RequestViewModel

@Composable
fun ServerScreen(vm: RequestViewModel, navController: NavController) {
    var servers by remember { mutableStateOf(DataArray.empty()) }

    LaunchedEffect(true) {
        // this will fetch the server list from server
        // preferably, if server list not seen before, download it to disk for better loading experience
        // if downloaded to disk, load servers from there
        // vim.getArray { url("https://discord.com/api/v9/users/@me/affinities/guilds") }
    }

    Column {
        Text("Servers")
    }
}