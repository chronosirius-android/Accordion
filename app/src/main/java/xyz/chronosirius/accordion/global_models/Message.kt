package xyz.chronosirius.accordion.global_models

import androidx.compose.runtime.Composable
import io.ktor.client.HttpClient
import xyz.chronosirius.accordion.data.DataObject

class Message(
    override val id: String,
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
    val call: DataObject?,
    val client: HttpClient
): Snowflaked() {

}