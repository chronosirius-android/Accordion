package xyz.chronosirius.accordion.directs

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import xyz.chronosirius.accordion.data.DataArray
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DirectMessageScreen(vm: DirectMessageViewModel, navController: NavController) {
    val isUnloaded by vm.isUnloaded.collectAsStateWithLifecycle()
    if (isUnloaded) {
        LaunchedEffect(true) {
            // This will fetch the direct messages from the server
            // and update the UI with the messages list
            // will load a conversation screen fragment with the messages once loaded
            // vm.getArray { url("https://discord.com/api/v9/users/@me/channels") }
            vm.getChannels()
        }
    }
    Column {

    }
}