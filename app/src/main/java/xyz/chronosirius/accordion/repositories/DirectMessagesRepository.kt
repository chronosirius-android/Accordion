package xyz.chronosirius.accordion.repositories

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.appendPathSegments
import xyz.chronosirius.accordion.DiscordApiClient
import xyz.chronosirius.accordion.global_models.DMChannel
import xyz.chronosirius.accordion.global_models.Message

class DirectMessagesRepository(
    private val client: DiscordApiClient,
) {
    suspend fun getDirectMessages(): List<DMChannel> {
        val rawDA = client.getArray(
            req = HttpRequestBuilder().apply {
                url {
                    appendPathSegments("users/@me/channels")
                }
            }
        )
        var channels = mutableListOf<DMChannel>()
        rawDA.forEachIndexed { index, _ ->
            channels.add(DMChannel.fromJson(rawDA.getObject(index)))
        }
        return channels
    }

    suspend fun getMessagesForChannel(channelId: Long): List<Message> {
        val rawDA = client.getArray(
            req = HttpRequestBuilder().apply {
                url {
                    appendPathSegments("channels", channelId.toString(), "messages")
                }
            }
        )
        var messages = mutableListOf<Message>()
        rawDA.forEachIndexed { i, sm ->
            val m = rawDA.getObject(i)
            messages.add(
                Message.fromJson(m)
            )
        }
        return messages
    }

    suspend fun getChannelById(id: Long): DMChannel {
        val rawDO = client.getObject(
            req = HttpRequestBuilder().apply {
                url {
                    appendPathSegments("channels", id.toString())
                }
            }
        )
        return DMChannel.fromJson(rawDO)
    }
}