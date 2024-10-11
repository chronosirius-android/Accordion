package xyz.chronosirius.accordion

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import io.ktor.client.*
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import xyz.chronosirius.accordion.data.DataObject
import xyz.chronosirius.accordion.models.ResumeData
import kotlin.system.exitProcess

enum class ErrorType(val t: Int) {
    NONE(0),
    WEBSOCKET_ERROR(1),
    OTHER_ERROR(2)
}

class DiscordGatewayService : LifecycleService() {

    private val client = HttpClient(OkHttp) {
        // Configure the client here
        install(WebSockets) {
            maxFrameSize = 4096
        }

    }


    private var supervisorJob = SupervisorJob(parent=null)
    companion object {
        val latestMessage = MutableLiveData(DataObject.empty())
        val isGatewayConnected = MutableLiveData(false)
        val wsCloseReason = MutableLiveData<CloseReason?>(null)
        val otherError = MutableLiveData<Exception?>(null)
        val errorType = MutableLiveData(ErrorType.NONE)
    }

    private val resumeData = ResumeData()

    private val testToken = ""
    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        TODO("Return the communication channel to the service.")
    }



    @OptIn(DelicateCoroutinesApi::class)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val channel = NotificationChannel(
            "gateway",
            "Discord Gateway Service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        lifecycleScope.launch {
            while (resumeData.shouldResume) {
                try {
                    client.webSocket(resumeData.url) {
                        // this: DefaultClientWebSocketSession
                        // Send text frame
                        suspend fun sendObject(obj: DataObject) {
                            Log.d("DiscordGatewayService sendObject", obj.toString())
                            this.send(Frame.Text(obj.toString()))
                        }
                        // HELLO START
                        val hello = DataObject.fromJson((incoming.receive() as Frame.Text).readText())
                        Log.d("DiscordGatewayService", hello.toString())
                        if (hello.getInt("op") != 10) {
                            Log.e("DiscordGatewayService", "Expected OP 10, got ${hello.getInt("op")}")
                            isGatewayConnected.value = false
                            exitProcess(1)
                        }
                        val heartbeatInterval = hello.getObject("d").getInt("heartbeat_interval")
                        // HELLO PROCESSING COMPLETE
                        sendObject(
                            DataObject.empty()
                                .put("op", 1)
                                .put("d", null)
                        )
                        // FIRST HEARTBEAT SENT
                        try {
                            // apparently I don't have to IDENTIFY if I'm resuming so theres a conditional here
                            if (!resumeData.shouldResume) {
                                sendObject(
                                    DataObject.empty()
                                        .put("op", 2)
                                        .put(
                                            "d", DataObject.empty()
                                                .put(
                                                    "token",
                                                    PreferenceManager.getDefaultSharedPreferences(
                                                        this@DiscordGatewayService
                                                    )
                                                        .getString(
                                                            "token",
                                                            testToken
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
                            } else {
                                sendObject(
                                    DataObject.empty()
                                        .put("op", 6)
                                        .put(
                                            "d", DataObject.empty()
                                                .put("token", PreferenceManager.getDefaultSharedPreferences(this@DiscordGatewayService).getString("token", testToken)!!)
                                                .put("session_id", resumeData.sessionId)
                                                .put("seq", resumeData.seq)
                                        )
                                )
                            }
                        } catch (e: Exception) {
                            isGatewayConnected.value = false
                            Log.e("DiscordGatewayService", e.stackTraceToString())
                            val cr = closeReason.await()
                            errorType.value = if (cr != null) ErrorType.WEBSOCKET_ERROR else ErrorType.OTHER_ERROR
                            otherError.value = e
                            wsCloseReason.value = cr
                        }

                        isGatewayConnected.value = true
                        val heartBeatJob = launch(Dispatchers.IO) { // Heartbeat code (must disconnect from incoming frames or will not get sent)
                            while (isGatewayConnected.value == true) {
                                delay(heartbeatInterval.toLong())
                                try {
                                    sendObject(
                                        DataObject.empty()
                                            .put("op", 1)
                                            .put("d", resumeData.seq)
                                    )
                                } catch (e: Exception) {
                                    Log.e("DiscordGatewayService", e.stackTraceToString())
                                    Log.e("DiscordGatewayService", "Real Line 113")
                                }
                            }
                        } // End heartBeatJob
                        for (frame in incoming) {
                            if (incoming.isClosedForReceive) {
                                Log.e("DiscordGatewayService", "Incoming is closed")
                                isGatewayConnected.value = false
                                heartBeatJob.cancel()
                                wsCloseReason.value = closeReason.await()
                                errorType.value = ErrorType.WEBSOCKET_ERROR
                                break
                            } else {
                                Log.d("DiscordGatewayService", "Incoming is open, yay!")
                            }
                            try {
                                val payload = DataObject.fromJson((frame as Frame.Text).readText())
                                resumeData.seq = payload.getInt("s", 0)
                                latestMessage.value = payload
                                //Log.d("DiscordGatewayService payload recv", payload.toString()) // Debugging
                                when (payload.getInt("op")) {
                                    1 -> {
                                        Log.d("DiscordGatewayService", "OP 1 received")
                                        sendObject(
                                            DataObject.empty()
                                                .put("op", 1)
                                                .put("d", resumeData.seq)
                                        )
                                    }
                                    7 -> {
                                        Log.d("DiscordGatewayService", "OP 7 received")
                                        heartBeatJob.cancel()
                                        isGatewayConnected.value = false
                                        val cr = CloseReason(CloseReason.Codes.SERVICE_RESTART, "OP 7 received")
                                        wsCloseReason.value = cr
                                        close(cr)
                                    }
                                    9 -> {
                                        Log.d("DiscordGatewayService", "OP 9 received")
                                        heartBeatJob.cancel()
                                        isGatewayConnected.value = false
                                        val cr = CloseReason(CloseReason.Codes.CLOSED_ABNORMALLY, "OP 9 received")
                                        wsCloseReason.value = cr
                                        resumeData.shouldResume = false
                                        close(cr)
                                    }
                                    0 -> {
                                        handleEvent(payload)
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("DiscordGatewayService", e.stackTraceToString())
                                Log.d("DiscordGatewayService", closeReason.await().toString())
                                isGatewayConnected.value = false
                            }
                        } // end for frame in incoming
                    } // end WebSocket
                } catch (e: Exception) {
                    isGatewayConnected.value = false
                    Log.e("DiscordGatewayService", e.stackTraceToString())
                    errorType.value = ErrorType.OTHER_ERROR
                    otherError.value = e
                }
                delay(5000)
            } // end while
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("InlinedApi")
    fun handleEvent(payload: DataObject) {
        Log.d("DiscordGatewayService", "Handling event")
        when (payload.getString("t")) {
            "READY" -> {
                Log.d("DiscordGatewayService", "READY event received")
                resumeData.url = payload.getObject("d").getString("resume_gateway_url")
                resumeData.sessionId = payload.getObject("d").getString("session_id")
                resumeData.shouldResume = true
                Log.d("DiscordGatewayService", "Resume data: $resumeData")
                Log.d("DiscordGatewayService/DISPATCH", "READY! $payload")
                ServiceCompat.startForeground(
                    this,
                    1,
                    NotificationCompat.Builder(this, "gateway")
                        .setContentTitle("Discord Gateway Service")
                        .setContentText("Connected to Discord Gateway")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .build(),
                    FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
            }
            "MESSAGE_CREATE" -> {
                Log.d("DiscordGatewayService/DISPATCH", "MESSAGE_CREATE event received content:${payload.getObject("d").getString("content")}")
            }
            else -> {
                Log.d("DiscordGatewayService", "Unknown event received")
            }
        }
    }

    override fun onDestroy() {
        supervisorJob.cancel()
        lifecycleScope.cancel()
        super.onDestroy()
    }
}