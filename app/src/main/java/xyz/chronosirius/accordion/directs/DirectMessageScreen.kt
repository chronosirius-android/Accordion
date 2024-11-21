package xyz.chronosirius.accordion.directs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
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
    Column (
        Modifier.fillMaxWidth().absolutePadding(10.dp, 10.dp, 10.dp, 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    )
    {
        Row (modifier = Modifier.padding(10.dp)) {
            Text("Messages", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(R.drawable.search),
                contentDescription = "Search"
            )
        }
        Spacer(Modifier.height(10.dp))
        Row (modifier = Modifier.padding(5.dp).height(45.dp)) {
            Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                Icon(
                    painter = painterResource(R.drawable.person),
                    contentDescription = "Profile Picture"
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("chronosirius", fontWeight = FontWeight.Bold, modifier = Modifier.wrapContentSize(unbounded = true))
                Text("user status", fontSize = 3.em, modifier = Modifier.wrapContentSize(unbounded = true))
            }
            Spacer(modifier = Modifier.weight(1f))
            Text("18m")
        }
    }
}