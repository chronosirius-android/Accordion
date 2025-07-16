package xyz.chronosirius.accordion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em


@Composable
fun LoginScreen(callback: (String) -> Unit) {
    var token by remember { mutableStateOf("") }
    Scaffold { pv ->
        Column(
            modifier = Modifier.padding(pv).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Enter your Discord token to login",
                fontSize = 5.em
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = token,
                onValueChange = { token = it },
                label = { Text("Token") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                callback(token)
            }) {
                Text("Login")
            }
        }
    }
}