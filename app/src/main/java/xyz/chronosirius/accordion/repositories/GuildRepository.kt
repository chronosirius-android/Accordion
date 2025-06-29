package xyz.chronosirius.accordion.repositories

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.appendPathSegments
import xyz.chronosirius.accordion.DiscordApiClient
import xyz.chronosirius.accordion.global_models.Guild

class GuildRepository(
    private val client: DiscordApiClient,
) {
    suspend fun getGuilds(): List<Guild> {
        // This method will be implemented to load servers from the Discord API
        // For now, it returns an empty list
        val rawDA = client.getArray {
            url {
                appendPathSegments("users/@me/guilds")
            }
        }


        val guilds = mutableListOf<Guild>()
        rawDA.forEachIndexed { index, _ ->
            guilds.add(Guild.fromJson(rawDA.getObject(index)))
        }
        return guilds
    }
}