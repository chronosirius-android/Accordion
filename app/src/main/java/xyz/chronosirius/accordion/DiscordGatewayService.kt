package xyz.chronosirius.accordion

import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.ktor.client.*
import io.ktor.client.plugins.websocket.WebSockets
import androidx.lifecycle.lifecycleScope
import io.ktor.client.plugins.websocket.webSocket
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import androidx.preference.PreferenceManager
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.delay
import xyz.chronosirius.accordion.data.DataObject
import java.util.LinkedList
import java.util.Queue
import kotlin.system.exitProcess

class DiscordGatewayService : LifecycleService() {

    private val client = HttpClient(OkHttp) {
        // Configure the client here
        install(WebSockets) {
            maxFrameSize = 4096
        }

    }


    private var supervisorJob = SupervisorJob(parent=null)
    companion object {
        val latestMessage: MutableLiveData<DataObject> = MutableLiveData(DataObject.empty())
        val isGatewayConnected: MutableLiveData<Boolean> = MutableLiveData(false)
    }
    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        TODO("Return the communication channel to the service.")
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val serviceJob = lifecycleScope.launch {
            var seq = 0
            while (true) {
                try {
                    client.webSocket("wss://gateway.discord.gg/?v=9&encoding=json") {
                        // this: DefaultClientWebSocketSession
                        // Send text frame
                        suspend fun sendObject(obj: DataObject) {
                            Log.d("DiscordGatewayService sendObject", obj.toString())
                            this.send(Frame.Text(obj.toString()))
                        }

                        val hello = DataObject.fromJson((incoming.receive() as Frame.Text).readText())
                        Log.d("DiscordGatewayService", hello.toString())
                        if (hello.getInt("op") != 10) {
                            Log.e("DiscordGatewayService", "Expected OP 10, got ${hello.getInt("op")}")
                            isGatewayConnected.value = false
                            exitProcess(1)
                        }
                        val heartbeatInterval = hello.getObject("d").getInt("heartbeat_interval")
                        var lastBeat = System.currentTimeMillis()
                        // HELLO PROCESSING COMPLETE
                        sendObject(
                            DataObject.empty()
                                .put("op", 1)
                                .put("d", null)
                        )
                        // FIRST HEARTBEAT SENT
                        try {
                            sendObject(
                                DataObject.empty()
                                    .put("op", 2)
                                    .put(
                                        "d", DataObject.empty()
                                            .put(
                                                "token",
                                                PreferenceManager.getDefaultSharedPreferences(this@DiscordGatewayService)
                                                    .getString(
                                                        "token",
                                                        ""
                                                    )!!
                                            )
                                            .put("intents", 513)
                                            .put(
                                                "properties", DataObject.empty()
                                                    .put("os", "android")
                                                    .put("browser", "accordion")
                                                    .put("device", "unknown")
                                            )
                                    )
                            )

                        } catch (e: Exception) {
                            sendBroadcast(Intent("xyz.chronosirius.accordion.DISCORD_GATEWAY_ERROR"))
                            isGatewayConnected.value = false
                        }
                        isGatewayConnected.value = true
                        for (frame in incoming) {
                            isGatewayConnected.value = false
                            isGatewayConnected.value = true
                            if (System.currentTimeMillis() - lastBeat > heartbeatInterval) {
                                try {
                                    sendObject(
                                        DataObject.empty()
                                            .put("op", 1)
                                            .put("d", seq)
                                    )
                                } catch (e: Exception) {
                                    Log.e("DiscordGatewayService", e.stackTraceToString())
                                    Log.e("DiscordGatewayService", "Real Line 113")
                                }
                                lastBeat = System.currentTimeMillis();
                            }

                            if (incoming.isClosedForReceive) {
                                Log.e("DiscordGatewayService", "Incoming is closed")
                                sendBroadcast(Intent("xyz.chronosirius.accordion.DISCORD_GATEWAY_ERROR"))
                                isGatewayConnected.value = false
                                break
                            } else {
                                Log.d("DiscordGatewayService", "Incoming is open")
                            }
                            try {
                                val payload =
                                    DataObject.fromJson((frame as Frame.Text).readText())
                                seq = payload.getInt("s", 0)
                                latestMessage.value = payload
                                Log.d("DiscordGatewayService", payload.toString()) // Debugging
                            } catch (e: Exception) {
                                Log.e("DiscordGatewayService", e.stackTraceToString())
                                Log.e("DiscordGatewayService", "Real Line amongus")
                                Log.d("DiscordGatewayService", closeReason.await().toString())

                                isGatewayConnected.value = false
                            }
                        } // end for frame in incoming
                    } // end WebSocket
                } catch (e: Exception) {
                    isGatewayConnected.value = false
                }
                delay(1000)
            } // end while
        }
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onDestroy() {
        supervisorJob.cancel()
        super.onDestroy()
    }
}