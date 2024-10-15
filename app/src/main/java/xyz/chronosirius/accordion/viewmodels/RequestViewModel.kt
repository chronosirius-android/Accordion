package xyz.chronosirius.accordion.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.headers
import io.ktor.util.appendIfNameAbsent
import xyz.chronosirius.accordion.data.DataArray
import xyz.chronosirius.accordion.data.DataObject

class RequestViewModel: ViewModel() {
    // This class is used to hold data that needs to be shared between different parts of the app
    // It is a singleton class, meaning that only one instance of it will exist in the app
    // This is useful for storing data that needs to be accessed from different parts of the app
    // without having to pass it around as arguments
    companion object {
        // The data that needs to be shared
        var token: String = ""
    }
    var isRequesting = MutableLiveData<Boolean>(false)

    private val client = HttpClient(OkHttp) {
        install(DefaultRequest) {
            headers {
                appendIfNameAbsent(HttpHeaders.Authorization, token)
                appendIfNameAbsent(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }
            url("https://discord.com/api/v9")
        }
    }

    suspend fun getObject(req: HttpRequestBuilder, onSuccess: (DataObject) -> Unit, onError: (Throwable) -> Unit) {
        isRequesting.postValue(true)
        try {
            val res = client.get(req)
            isRequesting.postValue(false)
            onSuccess(DataObject.fromJson(res.bodyAsBytes()))
        } catch (e: Throwable) {
            isRequesting.postValue(false)
            onError(e)
        }
    }

    suspend fun getArray(req: HttpRequestBuilder, onSuccess: (DataArray) -> Unit, onError: (Throwable) -> Unit) {
        isRequesting.postValue(true)
        try {
            val res = client.get(req)
            isRequesting.postValue(false)
            onSuccess(DataArray.fromJson(res.bodyAsText()))
        } catch (e: Throwable) {
            isRequesting.postValue(false)
            onError(e)
        }
    }
}