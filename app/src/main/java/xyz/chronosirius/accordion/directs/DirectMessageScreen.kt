package xyz.chronosirius.accordion.directs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScopeInstance.weight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import xyz.chronosirius.accordion.data.DataArray
import androidx.lifecycle.viewmodel.compose.viewModel
import xyz.chronosirius.accordion.R

@Composable
fun DirectMessageScreen(navController: NavController) {
    /*val vm = viewModel<DirectMessageViewModel>()
    val isUnloaded by vm.isUnloaded.collectAsStateWithLifecycle()
    if (isUnloaded) {
        LaunchedEffect(true) {
            // This will fetch the direct messages from the server
            // and update the UI with the messages list
            // will load a conversation screen fragment with the messages once loaded
            // vm.getArray { url("https://discord.com/api/v9/users/@me/channels") }
            vm.getChannels()
        }
    }*/
    Row (modifier = Modifier.padding(10.dp)) {
        Text("Messages", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(R.drawable.search),
            contentDescription = "Search"
        )
    }
    Column { // this will act as the column that contains all the messages?
        Row (modifier = Modifier.padding(5.dp)) {
            Text("chronosirius", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text("18m", modifier = Modifier.size(10.dp))
        }
    }
}