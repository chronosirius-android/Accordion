package xyz.chronosirius.accordion.directs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
import xyz.chronosirius.accordion.R
import xyz.chronosirius.accordion.viewmodels.AccordionViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(navController: NavController, channelId: Long, vm: AccordionViewModel) {
    LaunchedEffect(Unit) {
        // This will fetch the direct messages from the server
        // and update the UI with the messages list
        // will load a conversation screen fragment with the messages once loaded
        // vm.get { url("https://discord.com/api/v9/users/@me/channels") }
        //vm.getMessages(channel)
        vm.loadDirectMessages(channelId)
    }
    val channel = vm.channels.find {
        it.id.toLong() == channelId
    }!!
    var message by remember {
        mutableStateOf("")
    }
    val interactionSource = remember { MutableInteractionSource() }
    Scaffold( topBar = @Composable {
        Row(modifier = Modifier.padding(20.dp)) {
            Text(channel.name(), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(R.drawable.call),
                contentDescription = "Call"
            )
        }
    }, bottomBar = {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.SpaceAround,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier
                    .padding(10.dp)
                    .size(30.dp)
                    .background(MaterialTheme.colorScheme.surfaceDim, shape = CircleShape)
                    .clickable {
                        // TODO: Open Menu for attachments and other things
                    }
            )
            //Spacer(modifier = Modifier.weight(1f))
            var tvm by remember {
                mutableStateOf(
                    TextFieldValue(
                        text = message,
                        selection = TextRange(message.length)
                    )
                )
            }
            BasicTextField(
                value = tvm,
                onValueChange = { message = it.text; tvm = it },
                interactionSource = interactionSource,
                singleLine = false,
                modifier = Modifier.fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(20.dp))
                    .height(30.dp),
                textStyle = LocalTextStyle.current.merge(TextStyle(color = LocalContentColor.current)),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            ) { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = message,
                    visualTransformation = VisualTransformation.None,
                    innerTextField = innerTextField,
                    singleLine = false,
                    enabled = true,
                    interactionSource = interactionSource,
                    contentPadding = PaddingValues(12.dp, 1.dp, 12.dp, 0.dp), // this is how you can remove the padding
                    placeholder = @Composable {
                        Text("Message ${channel.name()}", maxLines = 1, overflow = TextOverflow.Ellipsis, modifier=Modifier.padding(0.dp))
                    }
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceDim, shape = CircleShape)
                .clickable {
                    // TODO: Open Menu for attachments and other things
                }
                .size(30.dp)
                .padding(0.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.send),
                    contentDescription = "Add",
                    modifier = Modifier.absolutePadding(0.dp).size(26.dp)
                )
            }
        }
    }) {
        LazyColumn(modifier = Modifier.padding(it), reverseLayout = true) {
            stickyHeader {
                Spacer(modifier = Modifier.height(24.dp))
            }
            items(vm.messages.size) { index ->
                val message = vm.messages[index]
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .absolutePadding(10.dp, 10.dp, 10.dp, 0.dp),
                ) {
                    Text("${message.author.username}: ${message.content}", fontSize = 4.em)
                }
            }
        }
    }
}