package xyz.chronosirius.accordion

import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import io.ktor.client.*
import io.ktor.client.plugins.websocket.WebSockets
import androidx.lifecycle.lifecycleScope
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.client.plugins.websocket.webSocket
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import androidx.preference.PreferenceManager
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import java.util.LinkedList
import java.util.Queue

class DiscordGatewayService : LifecycleService() {

    private val client = HttpClient() {
        // Configure the client here
        install(WebSockets) {
            maxFrameSize = 4096
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }

    }

    private var supervisorJob = SupervisorJob(parent=null)
    companion object {
        val queue: Queue<JsonObject> = LinkedList()
    }
    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        TODO("Return the communication channel to the service.")
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val serviceJob = lifecycleScope.launch {
            client.webSocket("wss://gateway.discord.gg/?v=9&encoding=json") {
                // this: DefaultClientWebSocketSession
                // Send text frame
                val startFrame = incoming.receive() as Frame.Text
                val startPayload = Json.parseToJsonElement(startFrame.readText())
                Log.d("DiscordGatewayService", startPayload.toString()) // Debugging
                val heartbeat_interval = startPayload.jsonObject["d"]?.jsonObject?.get("heartbeat_interval")?.jsonObject.toString().toLong();
                var last_beat = System.currentTimeMillis();
                sendSerialized(
                    mapOf("op" to 1,
                        "d" to 0
                    )
                )
                sendSerialized(
                    mapOf("op" to 2,
                        "d" to mapOf(
                            "token" to PreferenceManager.getDefaultSharedPreferences(
                                this@DiscordGatewayService)
                                .getString("token", ""),
                            "intents" to 513,
                            "properties" to mapOf(
                                "os" to "android",
                                "browser" to "accordion",
                                "device" to "accordion"
                            )
                        ),
                        "s" to null,
                        "t" to null
                    )
                )
                var seq = null;
                while (true) {
                    if (System.currentTimeMillis() - last_beat > heartbeat_interval) {
                        sendSerialized(
                            mapOf("op" to 1,
                                "d" to seq
                            )
                        )
                        last_beat = System.currentTimeMillis();
                    }

                    val frame = incoming.receive() as Frame.Text
                    val payload = Json.parseToJsonElement(frame.readText())
                    Log.d("DiscordGatewayService", payload.toString()) // Debugging
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onDestroy() {
        supervisorJob.cancel()
        super.onDestroy()
    }
}