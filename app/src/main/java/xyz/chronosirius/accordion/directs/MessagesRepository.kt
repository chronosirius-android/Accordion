package xyz.chronosirius.accordion.directs

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.util.appendIfNameAbsent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import xyz.chronosirius.accordion.data.DataObject
import xyz.chronosirius.accordion.global_models.Message

class MessagesRepository(
    private val token: String
) {
    private var currentChannel: Int = 0
    private val client = HttpClient(OkHttp) {
        install(DefaultRequest) {
            headers {
                appendIfNameAbsent(HttpHeaders.Authorization, token)
                appendIfNameAbsent(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }
            url("https://discord.com/api/v9")
        }
    }
    val loading: Flow<Boolean> = flow { emit(false) }
    private val messages: MutableStateFlow<List<Message>> = MutableStateFlow(arrayListOf())

    suspend fun getMessages() {
        // This will fetch the direct messages from the server
        // and update the UI with the messages list
        // will load a conversation screen fragment with the messages once loaded
        // vm.get { url("https://discord.com/api/v9/users/@me/channels") }
        if (currentChannel == 0) {
            throw IllegalStateException("Channel ID not set")
        }

        val res = client.get {
            url("channels/$currentChannel/messages")
            parameter("limit", 50)
        }
        val payload = DataObject.fromJson(res.bodyAsBytes())
        val messagesInChannel = payload.getArray("messages")
        messagesInChannel
    }

    fun setCurrentChannel(channelId: Int) {
        // This will set the current channel ID
        currentChannel = channelId
    }
}