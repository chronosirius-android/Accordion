package xyz.chronosirius.accordion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class) // For certain Material 3 components if needed
@Composable
fun MessageInput(
    textFieldValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    voiceClick: () -> Unit,
    sendClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            //.background(Color(0xFF1E1E1E)) // Dark background color
            .background(MaterialTheme.colorScheme.surfaceContainerHighest) // Use Material theme surface color
            .padding(horizontal = 16.dp, vertical = 8.dp)
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween // Distribute items horizontally
    ) {
        // Plus icon and "Text message" input area
        Row(
            modifier = Modifier
                .weight(1f) // Takes up available space
                .clip(RoundedCornerShape(24.dp)) // Rounded corners for the input area
                .background(MaterialTheme.colorScheme.surfaceVariant) // Slightly lighter background for the input
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add attachment",
                tint = MaterialTheme.colorScheme.onSurfaceVariant, // Lighter grey tint
                modifier = Modifier
                    .size(24.dp)
                    .clickable { /* Handle add attachment click */ }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(modifier = Modifier.weight(1f)) {
                if (textFieldValue.text.isEmpty()) {
                    Text(
                        text = "Message here...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant, // Lighter grey for hint text
                        fontSize = 16.sp
                    )
                }
                BasicTextField(
                    value = textFieldValue,
                    onValueChange = onValueChange, // White text input
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary), // White cursor,
                    textStyle = LocalTextStyle.current.merge(TextStyle(color = LocalContentColor.current))
                )
            }


            Spacer(modifier = Modifier.width(8.dp))

            // Emoji icon
            Icon(
                painter = painterResource(R.drawable.add_emoji),
                contentDescription = "Emoji",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(28.dp) // Slightly larger icons
                    .clickable { /* Handle emoji click */ }
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Voice/Send icon
        //val c = LocalContext.current
        if (textFieldValue.text.isBlank()) {
            Icon(
                painter = painterResource(R.drawable.mic_outlined),
                contentDescription = "Voice message",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        voiceClick()
                    }
            )
        } else {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape) // Background for send icon
                    .clickable {
                        sendClick()
                    },
                contentAlignment = Alignment.Center // Center the icon in the box
            ) {
                Icon(
                    painter = painterResource(R.drawable.send),
                    contentDescription = "Send message",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(40.dp) // Smaller icon inside the box
                )
            }
        }
    }
}