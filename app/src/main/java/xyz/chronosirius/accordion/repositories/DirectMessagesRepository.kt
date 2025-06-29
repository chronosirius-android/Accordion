package xyz.chronosirius.accordion.repositories

import android.util.Log
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.headers
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.appendPathSegments
import xyz.chronosirius.accordion.DiscordApiClient
import xyz.chronosirius.accordion.data.DataObject
import xyz.chronosirius.accordion.global_models.DMChannel
import xyz.chronosirius.accordion.global_models.Message

class DirectMessagesRepository(
    private val client: DiscordApiClient,
) {
    suspend fun getDirectMessages(): List<DMChannel> {
        val rawDA = client.getArray {
            url {
                appendPathSegments("users/@me/channels")
            }
        }
        val channels = mutableListOf<DMChannel>()
        rawDA.forEachIndexed { index, _ ->
            channels.add(DMChannel.fromJson(rawDA.getObject(index)))
        }
        return channels
    }

    suspend fun getMessagesForChannel(channelId: Long): List<Message> {
        val rawDA = client.getArray {
            url {
                appendPathSegments("channels", channelId.toString(), "messages")
            }
        }
        val messages = mutableListOf<Message>()
        rawDA.forEachIndexed { i, _ ->
            val m = rawDA.getObject(i)
            messages.add(
                Message.fromJson(m)
            )
        }
        return messages
    }

    suspend fun getChannelById(id: Long): DMChannel {
        val rawDO = client.getObject {
            url {
                appendPathSegments("channels", id.toString())
            }
        }
        return DMChannel.fromJson(rawDO)
    }

    suspend fun sendMessage(channelId: Long, text: String): Message {
        val res = client.post(
            req = HttpRequestBuilder().apply {
                method = io.ktor.http.HttpMethod.Post
                url {
                    appendPathSegments("channels", channelId.toString(), "messages")
                }
                headers {
                    append("Content-Type", "application/json")
                }
                setBody(
                    DataObject.empty()
                        .put("content", text)
                        .toString()
                )
            }
        )
        Log.d("DirectMessagesRepository", "sendMessage response: ${res.status.value} ${res.bodyAsText()}")
        if (res.status.value != 200) {
            throw Exception("Failed to send message: ${res.bodyAsText()}")
        }
        return Message.fromJson(DataObject.fromJson(res.bodyAsText()))
    }
}