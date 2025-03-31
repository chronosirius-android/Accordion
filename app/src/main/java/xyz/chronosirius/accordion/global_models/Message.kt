package xyz.chronosirius.accordion.global_models

import io.ktor.client.HttpClient
import xyz.chronosirius.accordion.data.DataObject

class TempMessage(
    override val id: Long,
    val content: String,
    val channelId: String,
    val author: UserBase,
    val editedTimestamp: String?,
    val flags: Int
): Snowflaked() {
    companion object {
        fun fromJson(da: DataObject): TempMessage {
            return TempMessage(
                da.getLong("id"),
                da.getString("content"),
                da.getString("channel_id"),
                User.fromJson(da.getObject("author")),
                da.getString("edited_timestamp", null),
                da.getInt("flags")
            )
        }
    }
}

class Message(
    override val id: Long,
    val channelId: String,
    val author: UserBase,
    val content: String,
    val timestamp: String,
    val editedTimestamp: String?,
    val tts: Boolean,
    val mentionEveryone: Boolean,
    val mentions: List<UserBase>,
    val mentionRoles: List<String>,
    val mentionChannels: List<String>?,
    val attachments: List<DataObject>,
    val embeds: List<DataObject>,
    val reactions: List<DataObject>?,
    val nonce: String?,
    val pinned: Boolean,
    val webhookId: String?,
    val type: Int,
    val activity: DataObject?,
    val application: DataObject?,
    val applicationId: String?,
    val flags: Int,
    val messageReference: DataObject?,
    val messageSnapshots: List<DataObject>?,
    val referencedMessage: Message?,
    val interactionMetadata: DataObject?,
    val thread: DataObject?,
    val components: List<DataObject>?,
    val stickerItems: List<DataObject>?,
    val stickers: List<DataObject>?,
    val position: Int?,
    val roleSubscriptionData: DataObject?,
    val resolved: DataObject?,
    val poll: DataObject?,
    val call: DataObject?
): Snowflaked() {
    companion object {
        fun fromJson(data: DataObject): Message {
            return Message(
                id = data.getLong("id"),
                channelId = data.getString("channel_id"),
                author = User.fromJson(data.getObject("author")),
                content = data.getString("content"),
                timestamp = data.getString("timestamp"),
                editedTimestamp = data.getString("edited_timestamp"),
                tts = false,
                mentionEveryone = data.getBoolean("mention_everyone"),
                mentions = data.getString("mentions").let { mentions ->
                    if (mentions.isEmpty()) {
                        emptyList()
                    } else {
                        mentions.split(",").map { User.fromJson(data.getObject(it)) }
                    }
                },
                mentionRoles = TODO(),
                mentionChannels = TODO(),
                attachments = TODO(),
                embeds = TODO(),
                reactions = TODO(),
                nonce = TODO(),
                pinned = TODO(),
                webhookId = TODO(),
                type = TODO(),
                activity = TODO(),
                application = TODO(),
                applicationId = TODO(),
                flags = TODO(),
                messageReference = TODO(),
                messageSnapshots = TODO(),
                referencedMessage = TODO(),
                interactionMetadata = TODO(),
                thread = TODO(),
                components = TODO(),
                stickerItems = TODO(),
                stickers = TODO(),
                position = TODO(),
                roleSubscriptionData = TODO(),
                resolved = TODO(),
                poll = TODO(),
                call = TODO(),
            )
        }
    }

}

