package xyz.chronosirius.accordion.servers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import xyz.chronosirius.accordion.R
import xyz.chronosirius.accordion.data.DataArray

@Composable
fun ServerScreen(navController: NavController) {
    var servers by remember { mutableStateOf(DataArray.empty()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(true) {
        // this will fetch the server list from server
        // preferably, if server list not seen before, download it to disk for better loading experience
        // if downloaded to disk, load servers from there
        // nty actually dont save server list to disk, just fetch it from server
        // nvm I'm using a viewmodel for this but i need this comment so i can remember what i was thinking
        // vim.getArray { url("https://discord.com/api/v9/users/@me/affinities/guilds") }
    }

    Column {
        Text("Servers")
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(R.drawable.search),
            contentDescription = "Search"
        )
    }
}