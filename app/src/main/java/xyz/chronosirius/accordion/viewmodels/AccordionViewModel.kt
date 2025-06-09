package xyz.chronosirius.accordion.viewmodels


import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.url
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import xyz.chronosirius.accordion.DiscordApiClient
import xyz.chronosirius.accordion.R
import xyz.chronosirius.accordion.global_models.DMChannel
import xyz.chronosirius.accordion.global_models.Message
import java.io.File

class AccordionViewModel: ViewModel() {
    // This class is used to hold data that needs to be shared between different parts of the app
    // It is a singleton class, meaning that only one instance of it will exist in the app
    // This is useful for storing data that needs to be accessed from different parts of the app
    // without having to pass it around as arguments
    companion object {
        // The data that needs to be shared
        var token: String = ""
    }
    private val _isRequesting = MutableStateFlow<Boolean>(false)
    val isRequesting = _isRequesting.asStateFlow()
    private val _error: MutableStateFlow<Throwable?> = MutableStateFlow(null);
    val error = _error.asStateFlow()
    private val _isPulling = MutableStateFlow<Boolean>(false)
    val isPulling = _isPulling.asStateFlow()

    private val _channels = mutableStateListOf<DMChannel>()
    val channels: List<DMChannel> = _channels
    
    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> = _messages

    private val client = DiscordApiClient()

//    suspend fun getDMChannels(isPull: Boolean = false) {
//        this._isPulling.emit(isPull)
//        client.getArray(
//            req = HttpRequestBuilder().apply {
//                url {
//                    appendPathSegments("users/@me/channels")
//                }
//            },
//            onSuccess = {
//                this._channels.clear()
//                it.forEachIndexed { index, _ ->
//                    this._channels.add(DMChannel.fromJson(it.getObject(index)))
//                }
//                this._channels.sortWith(compareByDescending { it.lastMessageTimeUnix() })
//            }, onError = {}
//        )
//        this._isPulling.emit(false)
//    }



//    suspend fun getAvatarBitmap(objectId: Long, hash: String): Bitmap {
//        Log.d("AccordionViewModel", "objectId: $objectId, hash: $hash")
//        Log.d("AccordionViewModel", "avatarbitmap get called")
//        Log.d("AccordionViewModel", "https://cdn.discordapp.com/avatars/$objectId/$hash.webp?size=480")
//        val res = client.get {
//            url("https://cdn.discordapp.com/avatars/$objectId/$hash.png")
//            Log.d("AVM/Avatar", url.buildString())
//            headers {
//                append(HttpHeaders.Host, "cdn.discordapp.com")
//                //append(HttpHeaders.ContentType, ContentType.Image.PNG.toString())
//            }
//        }
//
//        delay(2000)
//
//        Log.d("AccordionViewModel/Avatar", res.bodyAsText())
//
//        val imageData = res.bodyAsBytes()
//        Log.d("AccordionViewModel/Avatar", imageData.size.toString())
//        Log.d("AccordionViewModel/Avatar", imageData.toString())
//        return BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
//    }
}