package xyz.chronosirius.accordion.directs

import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
import xyz.chronosirius.accordion.R
import xyz.chronosirius.accordion.viewmodels.AccordionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectMessageScreen(navController: NavController, vm: AccordionViewModel, ctx: Context) {
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

    var us by remember { mutableIntStateOf(1) }

    LaunchedEffect(us) {
        vm.getDMChannels(us != 1)
    }
    PullToRefreshBox(
        isRefreshing = vm.isPulling.collectAsState().value,
        onRefresh = {
            us += 1
        }
    ) {
        Column (
            Modifier.fillMaxWidth().absolutePadding(10.dp, 10.dp, 10.dp, 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        )
        {
            Row(modifier = Modifier.padding(10.dp)) {
                Text("Messages", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(R.drawable.search),
                    contentDescription = "Search"
                )
            }
            Spacer(Modifier.height(10.dp))

            LazyColumn {
                items(vm.channels.size) { index ->
                    val channel = vm.channels[index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .padding(5.dp)
                            .clickable {
                                navController.navigate("channels/${channel.id}") {
                                    launchSingleTop = true
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        channel.Icon(
                            vm, ctx,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Log.d(
                                "DirectMessageScreen",
                                "channel.id: ${channel.id} channel.recipients: ${channel.recipients}"
                            )
                            Text(
                                channel.name(),
                                fontWeight = FontWeight.Bold,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.width(
                                    200.dp
                                ),
                                maxLines = 1
                            )
                            Text("user status", fontSize = 3.em)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        channel.timeSinceLastMessage().also { since ->
                            var time: String =
                                if (since.years > 0) {
                                    "${since.years}y"
                                } else if (since.months > 0) {
                                    "${since.months}mo"
                                } else if (since.days > 0) {
                                    "${since.days}d"
                                } else if (since.hours > 0) {
                                    "${since.hours}h"
                                } else if (since.minutes > 0) {
                                    "${since.minutes}m"
                                } else {
                                    "now"
                                }
                            Text(
                                time,
                                fontSize = 3.em,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.absolutePadding(0.dp, 0.dp, 5.dp, 0.dp)
                                    .width(35.dp)
                            )
                        }

                    }
                }
            }
        }
//        Row (modifier = Modifier.padding(5.dp).height(45.dp)) {
//            Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
//                Icon(
//                    painter = painterResource(R.drawable.person),
//                    contentDescription = "Profile Picture"
//                )
//            }
//            Spacer(modifier = Modifier.width(8.dp))
//            Column {
//                Text("chronosirius", fontWeight = FontWeight.Bold, modifier = Modifier.wrapContentSize(unbounded = true))
//                Text("user status", fontSize = 3.em, modifier = Modifier.wrapContentSize(unbounded = true))
//            }
//            Spacer(modifier = Modifier.weight(1f))
//            Text("18m")
//        }
    }
}