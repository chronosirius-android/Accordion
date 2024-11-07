package xyz.chronosirius.accordion.directs

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
import xyz.chronosirius.accordion.R
import xyz.chronosirius.accordion.viewmodels.RequestViewModel

@Composable
fun ConversationScreen(channelId: Int, vm: RequestViewModel, navController: NavController) {
    LaunchedEffect(true) {
        // This will fetch the direct messages from the server
        // and update the UI with the messages list
        // will load a conversation screen fragment with the messages once loaded
        // vm.get { url("https://discord.com/api/v9/users/@me/channels") }
    }
    Row (modifier = Modifier.padding(5.dp)) {
        Text("Chronosirirus")
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(R.drawable.call),
            contentDescription = "Call"
        )
    }
}