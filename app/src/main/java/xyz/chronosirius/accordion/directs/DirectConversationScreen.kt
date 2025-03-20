package xyz.chronosirius.accordion.directs

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import xyz.chronosirius.accordion.R
import xyz.chronosirius.accordion.viewmodels.AccordionViewModel

@Composable
fun ConversationScreen(navController: NavController, channelId: Long, vm: AccordionViewModel) {
    LaunchedEffect(Unit) {
        // This will fetch the direct messages from the server
        // and update the UI with the messages list
        // will load a conversation screen fragment with the messages once loaded
        // vm.get { url("https://discord.com/api/v9/users/@me/channels") }
        //vm.getMessages(channel)
    }
    val channel = vm.channels.find {
        it.id.toLong() == channelId
    }
    Row (modifier = Modifier.padding(10.dp)) {
        Text(channel!!.name())
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(R.drawable.call),
            contentDescription = "Call"
        )
    }
}