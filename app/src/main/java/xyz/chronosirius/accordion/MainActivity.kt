package xyz.chronosirius.accordion

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import xyz.chronosirius.accordion.ui.theme.AccordionTheme

// UI components file
// https://developer.android.com/develop/ui/compose/documentation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AccordionTheme {
                // Jetpack Compose basically uses lambdas inside lambdas to create UI components
                // which the Android system will render
                // We never need to update these components manually, the system will do it for us
                // when it detects a change in a value it triggers a recomposition
                // and redraws the screen with the new values/data
                startService(Intent(this, DiscordGatewayService::class.java))
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                }
            }
        }
    }
}