package xyz.chronosirius.accordion.directs

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.util.appendIfNameAbsent

class MessagesDataSource {
    private val client = HttpClient(OkHttp) {
        install(DefaultRequest) {
            headers {
                appendIfNameAbsent(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }
            url("https://discord.com/api/v9")
        }
    }
}