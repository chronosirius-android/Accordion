package xyz.chronosirius.accordion.viewmodels


import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.appendPathSegments
import io.ktor.util.appendIfNameAbsent
import kotlinx.coroutines.flow.MutableStateFlow
import xyz.chronosirius.accordion.DiscordGatewayService
import xyz.chronosirius.accordion.data.DataArray
import xyz.chronosirius.accordion.data.DataObject
import xyz.chronosirius.accordion.global_models.DMChannel

class AccordionViewModel: ViewModel() {
    // This class is used to hold data that needs to be shared between different parts of the app
    // It is a singleton class, meaning that only one instance of it will exist in the app
    // This is useful for storing data that needs to be accessed from different parts of the app
    // without having to pass it around as arguments
    companion object {
        // The data that needs to be shared
        var token: String = ""
    }
    var isRequesting = MutableStateFlow<Boolean>(false)
    private var _channels = mutableStateListOf<DMChannel>()
    val channels: List<DMChannel> = _channels

    private val client = HttpClient(OkHttp) {
        install(DefaultRequest) {
            headers {
                appendIfNameAbsent(HttpHeaders.Authorization, DiscordGatewayService.testToken)
                appendIfNameAbsent(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                Log.d("AccordionViewModel", DiscordGatewayService.testToken)
                Log.d("AccordionViewModel", headers.build().toString())
            }
            url("https://discord.com/api/v9")
        }
    }

    suspend fun getObject(req: HttpRequestBuilder, onSuccess: (DataObject) -> Unit, onError: (Throwable) -> Unit) {
        isRequesting.emit(true)
        try {
            val res = client.get(req)
            isRequesting.emit(false)
            onSuccess(DataObject.fromJson(res.bodyAsBytes()))
        } catch (e: Throwable) {
            isRequesting.emit(false)
            onError(e)
        }
    }

    suspend fun getArray(req: HttpRequestBuilder, onSuccess: (DataArray) -> Unit, onError: (Throwable) -> Unit) {
        isRequesting.emit(true)
        try {
            val res = client.get(req)
            req.headers { appendIfNameAbsent(HttpHeaders.Authorization, DiscordGatewayService.testToken)
                appendIfNameAbsent(HttpHeaders.ContentType, ContentType.Application.Json.toString()) }
            Log.d("AccordionViewModel", req.build().headers.toString())
            isRequesting.emit(false)
            Log.d("AccordionViewModel", res.bodyAsText())
            onSuccess(DataArray.fromJson(res.bodyAsText()))
        } catch (e: Throwable) {
            isRequesting.emit(false)
            onError(e)
        }
    }

    suspend fun getDMChannels() {
        getArray(
            req = HttpRequestBuilder().apply {
                url {
                    appendPathSegments("users/@me/channels")
                }
            },
            onSuccess = {
                this._channels.clear()
                it.forEachIndexed { index, _ ->
                    this._channels.add(DMChannel.fromJson(it.getObject(index)))
                }
                this._channels.sortWith(compareByDescending { it.lastMessageTimeUnix() })
            }, onError = {
                throw it
            }
        )
    }
}