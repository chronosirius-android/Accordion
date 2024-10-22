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