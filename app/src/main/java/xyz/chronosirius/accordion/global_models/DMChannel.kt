package xyz.chronosirius.accordion.global_models

import xyz.chronosirius.accordion.data.DataObject
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

class DMChannel(
    override val id: Long,
    val type: Int,
    val lastMessageId: String,
    val flags: Int,
    val recipients: List<User>,
    val name: String?
    //val client: HttpClient
): Snowflaked() {
    // This class is used to hold data that needs to be shared between different parts of the app
    // It is a singleton class, meaning that only one instance of it will exist in the app
    // This is useful for storing data that needs to be accessed from different parts of the app
    // without having to pass it around as arguments
    companion object {
        fun fromJson(da: DataObject): DMChannel {
            return DMChannel(
                da.getLong("id"),
                da.getInt("type"),
                da.getString("last_message_id"),
                da.getInt("flags"),
                da.getObjectArray("recipients").map { User.Companion.fromJson(it) },
                da.getString("name", null)
            )
        }

        // The data that needs to be shared
        var token: String = ""

    }

    fun sendMessage(message: Message) {
        // This will send a message to the channel

    }

    fun name(): String {
        try {
            if (name != null) {
                return name
            } else {
                return recipients[0].username
            }
        } catch (_: Exception) {
            return id.toString()
        }
    }

    fun lastMessageTime(): LocalDateTime {
        val lastMessageUnix = (lastMessageId.toLong() shr 22) + 1420070400000
        val lastMessageInstant = Instant.ofEpochMilli(lastMessageUnix)
        return ZonedDateTime.ofInstant(lastMessageInstant, ZoneOffset.UTC).withZoneSameInstant(
            ZoneId.systemDefault()).toLocalDateTime()
    }

    fun lastMessageTimeUnix(): Long {
        return (lastMessageId.toLong() shr 22) + 1420070400000
    }
}