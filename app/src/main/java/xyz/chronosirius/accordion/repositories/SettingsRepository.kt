package xyz.chronosirius.accordion.repositories

import io.ktor.http.appendPathSegments
import xyz.chronosirius.accordion.DiscordApiClient
import xyz.chronosirius.accordion.data.DataArray
import xyz.chronosirius.accordion.data.DataObject

data class RawGuildFolder(
    val id: Long? = null,
    val name: String? = null,
    val color: Int? = null,
    val guildIds: List<String>
)
class SettingsRepository(
    private val client: DiscordApiClient
) {
    // This repository is used to manage settings related to the Discord API client
    // It can be used to fetch, update, or delete settings as needed

    // Example method to get a setting
    suspend fun getAllSettings(): DataObject {
        return client.getObject {
            url {
                appendPathSegments("users/@me/settings")
            }
        }
    }


    suspend fun getGuildFolders(): List<RawGuildFolder> {
        val rawDO = getAllSettings()
        return rawDO.getObjectArray("guild_folders").map {
            val guildIds = emptyList<String>().toMutableList()
            val s = it.getArray("guild_ids")
            s.forEachIndexed { index, _ ->
                guildIds += s.getString(index)
            }
            RawGuildFolder(
                id = it.getLongOrNull("id"),
                name = it.getString("name", null),
                color = it.getIntOrNull("color"),
                guildIds = guildIds
            )
        }
    }

    // Additional methods for managing settings can be added here
}