package xyz.chronosirius.accordion.directs

import androidx.lifecycle.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.util.appendIfNameAbsent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import xyz.chronosirius.accordion.global_models.User

class DirectMessageViewModel(token: String): ViewModel() {
    // This will fetch the direct messages from the server
    // and update the UI with the messages list
    // will load a conversation screen fragment with the messages once loaded
    // vm.getArray { url("https://discord.com/api/v9/users/@me/channels") }
    val channels: MutableStateFlow<List<DMChannel>> = MutableStateFlow(listOf<DMChannel>())
    val _isUnloaded = MutableStateFlow(true)
    val isUnloaded = _isUnloaded.asStateFlow()

    private val client = HttpClient(OkHttp) {
        install(DefaultRequest) {
            headers {
                appendIfNameAbsent(HttpHeaders.Authorization, token)
                appendIfNameAbsent(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }
            url("https://discord.com/api/v9")
        }
    }

    suspend fun getChannels() {
        val res = client.get("users/@me/channels")

    }
}

class DMChannel(
    val id: String,
    val type: Int,
    val lastMessageId: String,
    val flags: Int,
    val recipients: List<User>,
    val client: HttpClient
) {
    // This class is used to hold data that needs to be shared between different parts of the app
    // It is a singleton class, meaning that only one instance of it will exist in the app
    // This is useful for storing data that needs to be accessed from different parts of the app
    // without having to pass it around as arguments
    companion object {
        // The data that needs to be shared
        var token: String = ""
    }

//    fun sendMessage(message: Message) {
//        // This will send a message to the channel
//
//    }
}