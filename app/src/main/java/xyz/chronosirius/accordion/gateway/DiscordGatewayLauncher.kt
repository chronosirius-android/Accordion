package xyz.chronosirius.accordion.gateway

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class DiscordGatewayLauncher: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val serviceIntent = Intent(context, DiscordGatewayService::class.java)
        context?.startService(serviceIntent)
    }
}