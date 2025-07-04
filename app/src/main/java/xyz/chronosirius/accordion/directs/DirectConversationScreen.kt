package xyz.chronosirius.accordion.directs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import xyz.chronosirius.accordion.MessageInput
import xyz.chronosirius.accordion.R
import xyz.chronosirius.accordion.viewmodels.DirectMessageConversationViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    navController: NavController,
    vm: DirectMessageConversationViewModel
) {

    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(text = ""))
    }
    val interactionSource = remember { MutableInteractionSource() }

    val uiState = vm.uiState.collectAsState()
        .value

    val channel = uiState.channel
    val messages = uiState.messages

    Scaffold(topBar = @Composable {
        Row(modifier = Modifier.padding(20.dp)) {
            Text(channel.name(), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(R.drawable.call),
                contentDescription = "Call"
            )
        }
    }, bottomBar = {
        MessageInput(
            textFieldValue,
            onValueChange = { textFieldValue = it },
            voiceClick = {

            },
            sendClick = {
                if (textFieldValue.text.isNotBlank()) {
                    vm.sendMessage(textFieldValue.text)
                    textFieldValue = TextFieldValue(text = "")
                }
            }
        )
//        Row(verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceEvenly,
//            modifier = Modifier
//                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
//                .fillMaxWidth()
//                .imePadding()
//        ) {
//            Icon(
//                Icons.Default.Add,
//                contentDescription = "Add",
//                modifier = Modifier
//                    .padding(10.dp)
//                    .size(30.dp)
//                    .background(MaterialTheme.colorScheme.surfaceDim, shape = CircleShape)
//                    .clickable {
//                        // TODO: Open Menu for attachments and other things
//                    }
//            )
//            //Spacer(modifier = Modifier.weight(1f))
//            var tvm by remember {
//                mutableStateOf(
//                    TextFieldValue(
//                        text = message,
//                        selection = TextRange(message.length)
//                    )
//                )
//            }
//            BasicTextField(
//                value = tvm,
//                onValueChange = { message = it.text; tvm = it },
//                interactionSource = interactionSource,
//                singleLine = false,
//                modifier = Modifier.fillMaxWidth(0.8f)
//                    .clip(RoundedCornerShape(20.dp))
//                    .height(30.dp),
//                textStyle = LocalTextStyle.current.merge(TextStyle(color = LocalContentColor.current)),
//                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
//            ) { innerTextField ->
//                TextFieldDefaults.DecorationBox(
//                    value = message,
//                    visualTransformation = VisualTransformation.None,
//                    innerTextField = innerTextField,
//                    singleLine = false,
//                    enabled = true,
//                    interactionSource = interactionSource,
//                    contentPadding = PaddingValues(12.dp, 1.dp, 12.dp, 0.dp), // this is how you can remove the padding
//                    placeholder = @Composable {
//                        Text("Message ${channel.name()}", maxLines = 1, overflow = TextOverflow.Ellipsis, modifier=Modifier.padding(0.dp))
//                    }
//                )
//            }
            //MessageInput()
            //Spacer(modifier = Modifier.weight(1f))
//            Box(modifier = Modifier
//                .background(MaterialTheme.colorScheme.surfaceDim, shape = CircleShape)
//                .clickable {
//                    // TODO: Open Menu for attachments and other things
//                }
//                .size(30.dp)
//                .padding(0.dp),
//                contentAlignment = Alignment.Center,
//            ) {
//                Icon(
//                    Icons.Default.Send,
//                    contentDescription = "Send",
//                    modifier = Modifier
//                        .size(20.dp)
//                        .background(MaterialTheme.colorScheme.surfaceDim, shape = CircleShape)
//                        .clickable {
//                            // TODO: Open Menu for attachments and other things
//                        }
//                )
//            }
       // }
    }) {
        LazyColumn(modifier = Modifier.padding(it), reverseLayout = true) {
            stickyHeader {
                Spacer(modifier = Modifier.height(24.dp))
            }

            items(messages.size) { index ->
                val message = messages[index]
//                Row(
//                    modifier = Modifier
//                        .padding(10.dp)
//                        .absolutePadding(10.dp, 10.dp, 10.dp, 0.dp),
//                ) {
//                    Text("${message.author.username}: ${message.content}", fontSize = 4.em)
//                }
                message.UI(messages.getOrNull(index+1))
            }
        }
    }
}