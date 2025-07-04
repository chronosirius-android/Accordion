package xyz.chronosirius.accordion

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.util.appendIfNameAbsent
import kotlinx.coroutines.delay
import xyz.chronosirius.accordion.data.DataArray
import xyz.chronosirius.accordion.data.DataObject
import java.io.File


class DiscordApiClient {
    private val client = HttpClient(OkHttp) {
        defaultRequest {
            headers.appendIfNameAbsent(HttpHeaders.Authorization, DiscordGatewayService.testToken)
            headers.appendIfNameAbsent(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            //headers.appendIfNameAbsent(HttpHeaders.UserAgent, "Android/Accordion, Native Jetpack-Compose Material3, Kotlin2") // seems like discord doesn't like this UA (I got a warning), so until this becomes bigger, I will switch this to the discord chrome client UA
            headers.appendIfNameAbsent(HttpHeaders.UserAgent, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36")
            Log.d("AccordionViewModel", DiscordGatewayService.testToken)
            Log.d("AccordionViewModel", headers.build().toString())
            url("https://discord.com/api/v9")
        }
    }

    suspend fun getObject(req: HttpRequestBuilder.() -> Unit): DataObject {
        LoadingStateManager.incrementActiveRequests()
        val res = client.get(req)
        LoadingStateManager.decrementActiveRequests()
        return DataObject.fromJson(res.bodyAsBytes())
    }

    suspend fun getArray(req: HttpRequestBuilder.() -> Unit): DataArray {
        LoadingStateManager.incrementActiveRequests()
        val res = client.get(req)
        LoadingStateManager.decrementActiveRequests()
        return DataArray.fromJson(res.bodyAsText())
    }

    suspend fun getImageBitmap(iType: String, objectId: Long, hash: String, cacheDir: File): Bitmap {
        /*
        This function fetches an image from the Discord CDN and returns it as a Bitmap.
        LEGACY FUNCTION, DO NOT USE IN NEW CODE. (temporarily exists for compatibility with un-migrated code)
         */
        LoadingStateManager.incrementActiveRequests()
        Log.d("AccordionViewModel", "objectId: $objectId, hash: $hash")
        Log.d("AccordionViewModel", "imagebitmap get called")
        Log.d("AccordionViewModel", "https://cdn.discordapp.com/$iType/$objectId/$hash.webp")
        if (File(cacheDir, "${objectId}_$hash.webp").exists()) {
            Log.d("AVM/Image", "found file in cache: ${File(cacheDir, "${objectId}_$hash.webp").absolutePath}")
            LoadingStateManager.decrementActiveRequests()
            return BitmapFactory.decodeFile(File(cacheDir, "${objectId}_$hash.webp").absolutePath)
        }

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
        LoadingStateManager.decrementActiveRequests()
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
    }

    suspend fun post(req: HttpRequestBuilder): HttpResponse { // temporary legacy function, do not use in new code
        LoadingStateManager.incrementActiveRequests()
        val res = client.request(req)
        LoadingStateManager.decrementActiveRequests()
        Log.d("AccordionViewModel", "post response: ${res.status.value} ${res.bodyAsText()}")
        return res
    }
}