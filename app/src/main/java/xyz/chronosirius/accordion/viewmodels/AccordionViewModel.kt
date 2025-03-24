package xyz.chronosirius.accordion.viewmodels


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.appendPathSegments
import io.ktor.http.set
import io.ktor.util.appendIfNameAbsent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import xyz.chronosirius.accordion.DiscordGatewayService
import xyz.chronosirius.accordion.R
import xyz.chronosirius.accordion.data.DataArray
import xyz.chronosirius.accordion.data.DataObject
import xyz.chronosirius.accordion.global_models.DMChannel
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
    var isRequesting = MutableStateFlow<Boolean>(false)
    private var _channels = mutableStateListOf<DMChannel>()
    val channels: List<DMChannel> = _channels

    private val client = HttpClient(OkHttp) {
        defaultRequest {
            headers.appendIfNameAbsent(HttpHeaders.Authorization, DiscordGatewayService.testToken)
            headers.appendIfNameAbsent(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            headers.appendIfNameAbsent(HttpHeaders.UserAgent, "Android/Accordion, Native Jetpack-Compose Material3, Kotlin2")
            Log.d("AccordionViewModel", DiscordGatewayService.testToken)
            Log.d("AccordionViewModel", headers.build().toString())
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
//            req.headers { appendIfNameAbsent(HttpHeaders.Authorization, DiscordGatewayService.testToken)
//                appendIfNameAbsent(HttpHeaders.ContentType, ContentType.Application.Json.toString()) }
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

    suspend fun getImageBitmap(iType: String, objectId: Long, hash: String, cacheDir: File): Bitmap {
        isRequesting.emit(true)
        Log.d("AccordionViewModel", "objectId: $objectId, hash: $hash")
        Log.d("AccordionViewModel", "imagebitmap get called")
        Log.d("AccordionViewModel", "https://cdn.discordapp.com/$iType/$objectId/$hash.webp")
        if (File(cacheDir, "${objectId}_$hash.webp").exists()) {
            Log.d("AVM/Image", "found file in cache: ${File(cacheDir, "${objectId}_$hash.webp").absolutePath}")
            isRequesting.emit(false)
            Log.d("AndroidViewModel", "image request completed ${isRequesting.value}")
            return BitmapFactory.decodeFile(File(cacheDir, "${objectId}_$hash.webp").absolutePath)
        }

        Log.d("AndroidViewModel", "requesting image from cdn ${isRequesting.value}")
        val res = client.get {
            url("https://cdn.discordapp.com/$iType/$objectId/$hash.webp")
            Log.d("AVM/Image", url.buildString())
            headers {
                append(HttpHeaders.Host, "cdn.discordapp.com")
                //append(HttpHeaders.ContentType, ContentType.Image.PNG.toString())
            }
        }


        delay(2000)

        Log.d("AccordionViewModel/Image", "${res.status.value} ${res.request.url} ${res.bodyAsText()}")

        val imageData = res.bodyAsBytes()
        Log.d("AccordionViewModel/Image", "${res.request.url} imd size: ${imageData.size}")
        Log.d("AccordionViewModel/Image", "${res.request.url} imd: $imageData")
        File(cacheDir, "${objectId}_$hash.webp").writeBytes(imageData)

        Log.d("AVM/Image", "completed")
        isRequesting.emit(false)
        Log.d("AndroidViewModel", "image request completed ${isRequesting.value}")
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
    }

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

    suspend fun getAvatarBitmap(userId: Long, hash: String, cacheDir: File): Bitmap {
        // This will return the avatar for the user
        // based on their user ID and avatar hash
        // This is a placeholder implementation
        return getImageBitmap("avatars", userId, hash, cacheDir)
    }

    @DrawableRes
    fun getDefaultAvatar(userId: Long): Int {
        // This will return a default avatar for the user
        // based on their user ID
        var defaultAvatarIndex = (userId % 5)
        if (Math.random() < 0.01) {
            defaultAvatarIndex += (0..1).random()
        }

        return when (defaultAvatarIndex) {
            0L -> R.drawable.default_avatar_0
            1L -> R.drawable.default_avatar_1
            2L -> R.drawable.default_avatar_2
            3L -> R.drawable.default_avatar_3
            4L -> R.drawable.default_avatar_4
            else -> R.drawable.default_avatar_5
        }
    }
}