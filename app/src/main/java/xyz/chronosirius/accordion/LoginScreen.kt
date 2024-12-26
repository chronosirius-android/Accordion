package xyz.chronosirius.accordion

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlin.system.exitProcess


@Composable
fun LoginScreen(sharedPreferences: SharedPreferences, context: Context) {
    var token by remember { mutableStateOf("") }
    OutlinedTextField(
        value = token,
        onValueChange = { token = it },
        label = { "Token" }
    )
    Button(onClick = {
        sharedPreferences.edit().putString("token", token).apply()
        val mStartActivity = Intent(context, MainActivity::class.java)
        val mPendingIntentId = 123456
        val mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val mgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent)
        exitProcess(0);
    }) {
        Text("Login")
    }
}

@Composable
fun OnboardingScreen(onboardingStage: Int, setOnboardingStage: (Int) -> Unit) {
    val nc = rememberNavController()
    NavHost(navController = nc, startDestination = onboardingStage.toString()) {
        composable(0.toString()) {
            Text("Onboarding 0")
            Button(onClick = { setOnboardingStage(1) }) {
                Text("Next")
            }
            //TODO("actually implement this - this will likely be the 'paste your token here'/login screen after I intercept the login reqs")
            //TODO("actually I also need to download the user data from the server for caching purposes as well, idk how to give it ui tho...")
            //TODO("maybe I'll allow it to pass through onboarding and then if the coroutine is still running I leave a spinner at the end of onboarding until it loads the data?")
        }
        composable(1.toString()) {
            Text("Onboarding 1")
            Button(onClick = { setOnboardingStage(2) }) {
                Text("Next")
            }
            TODO("actually implement this - this will likely be the notification permission request")
        }

    }
}