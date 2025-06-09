package xyz.chronosirius.accordion.global_models

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import xyz.chronosirius.accordion.data.DataObject
import xyz.chronosirius.accordion.md.annotatedWith
import xyz.chronosirius.accordion.md.inlineTextContent
import xyz.chronosirius.accordion.md.rememberInlineContentPatternAnnotation
import xyz.chronosirius.accordion.md.rememberLinkPatternAnnotation
import xyz.chronosirius.accordion.md.richAnnotatedWith

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
                editedTimestamp = data.getString("edited_timestamp", null),
                tts = false,
                mentionEveryone = data.getBoolean("mention_everyone", false),
                mentions = data.getObjectArray("mentions").let { mentions ->
                    if (mentions.isEmpty()) {
                        emptyList()
                    } else {
                        mentions.map { User.fromJson(it) }
                    }
                },
                mentionRoles = data.getArray("mention_roles").let { roles ->
                    if (roles.isEmpty()) {
                        emptyList()
                    } else {
                        roles.map { it.toString() }
                    }
                },
                mentionChannels = try {
                    data.getArray("mention_channels").let { channels ->
                    if (channels.isEmpty()) {
                        emptyList()
                    } else {
                        channels.map { it.toString() }
                    }
                } } catch (_: Exception) {
                    emptyList()
                },
                attachments = data.getObjectArray("attachments").map { it },
                embeds = data.getObjectArray("embeds").map { it },
                reactions = try {
                    data.getObjectArray("reactions").map { it }
                } catch (_: Exception) {
                    emptyList()
                },
                nonce = data.getString("nonce", null),
                pinned = data.getBoolean("pinned", false),
                webhookId = data.getString("webhook_id", null),
                type = data.getInt("type"),
                activity = data.getObject("activity", null),
                application = data.getObject("application", null),
                applicationId = data.getString("application_id", null),
                flags = data.getInt("flags", 0),
                messageReference = data.getObject("message_reference", null),
                messageSnapshots = try {
                    data.getObjectArray("message_snapshots").map { it }
                } catch (_: Exception) {
                    emptyList()
                },
                referencedMessage = data.getObject("referenced_message", null)?.let { fromJson(it) },
                interactionMetadata = data.getObject("interaction_metadata", null),
                thread = data.getObject("thread", null),
                components = try {
                    data.getObjectArray("components").map { it }
                } catch (_: Exception) {
                    emptyList()
                },
                stickerItems = try {
                    data.getObjectArray("sticker_items").map { it }
                } catch (_: Exception) {
                    emptyList()
                },
                stickers = try {
                    data.getObjectArray("stickers").map { it }
                } catch (_: Exception) {
                    emptyList()
                },
                position = data.getInt("position", 0),
                roleSubscriptionData = data.getObject("role_subscription_data", null),
                resolved = data.getObject("resolved", null),
                poll = data.getObject("poll", null),
                call = data.getObject("call", null)
            )
        }
    }
    @Composable
    fun UI() {
        // This will be used to display the message in the UI
        // It will be a Composable function that will take the message data and display it
        // in a nice way
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                //Image(author)
                author.Avatar(modifier = Modifier
                    .clip(shape = CircleShape)
                    .size(30.dp)
                )
                Text(
                    text = author.username,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(5.dp)
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Content()
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun Content() {
        val p = "<@[0-9]+>"//.toRegex()
        var cont = this.content
        //val s = p.findAll(cont)

//        FlowRow(horizontalArrangement = Arrangement.spacedBy(0.dp), maxLines = 1) {
//            var l = 0
//            Spacer(modifier = Modifier.width(15.dp))
//            for (mr in s) {
//                Text(
//                    text = content.substring(l, mr.range.first),
//                    modifier = Modifier.padding(0.dp).alignByBaseline()
//                )
//                val id = mr.value.replace("<@", "").replace(">", "")
//                val user = this@Message.mentions.find { it.id.toString() == id }
//                if (user != null) {
//                    Text(text = "@${user.username}",
//                        fontWeight = FontWeight.Bold,
//                        modifier = Modifier
//                            .background(Color(0x4428D3F3),
//                                shape = RoundedCornerShape(4.dp))
//                            .alignByBaseline()
//                            .padding(0.dp)
//                    )
//                }
//                else {
//                    Text(mr.value, Modifier.padding(0.dp))
//                }
//                l = mr.range.last + 1
//            }
//            Text(content.substring(l, content.length),
//                modifier = Modifier.padding(0.dp).alignByBaseline()
//            )
//        }
//
//        val contentAnnotated: AnnotatedString = buildAnnotatedString {
//
//            var l = 0
//            for (mr in s) {
//                append(cont.substring(l, mr.range.first))
//                val id = mr.value.replace("<@", "").replace(">", "")
//                val user = this@Message.mentions.find { it.id.toString() == id }
//                if (user != null) {
//                    pushStyle(
//                        SpanStyle(
//                            fontWeight = FontWeight.Bold,
//                            background = Color(0x4428D3F3),
//
//                        )
//                    )
//                    append("@${user.username}")
//                    //appendInlineContent("mention", id)
//                    pop()
//                } else {
//                    append(mr.value)
//                }
//                l = mr.range.last + 1
//            }
//            append(cont.substring(l, cont.length))
//        }
//        Text(
//            text = contentAnnotated,
//            modifier = Modifier.padding(5.dp).padding(start=10.dp),
//            inlineContent = mapOf(
//                "mention" to InlineTextContent(
//                    placeholder = Placeholder(width = 20.em, height = 4.em, placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter),
//                    children = @Composable { l ->
//                        Text(
//                            text = "@${mentions.find{ it.id.toString() == l }?.username}",
//                            modifier = Modifier.padding(5.dp)
//                                .background(Color(0x4428D3F3), RoundedCornerShape(5.dp)),
//                        )
//                     }
//                )
//            )
//        )
        val autoLinkAnnotation = rememberLinkPatternAnnotation(
            pattern = "https?://[^ ]+",
            url = { it }
        )

        val tm = rememberTextMeasurer()
        val mentionTextStyle = LocalTextStyle.current.copy(
            fontWeight = FontWeight.Bold
        )
        val density = androidx.compose.ui.platform.LocalDensity.current.density
        val mentionAnnotation = rememberInlineContentPatternAnnotation(
            pattern = p
        ) { mt ->
            val mentionId = mt.replace("<@", "").replace(">", "").toLong()
            val user = this@Message.mentions.find { it.id == mentionId }
            Log.d("a", user.toString())

            if (user != null) {
                val l = tm.measure(
                    text = "@${user.username}",
                    style = mentionTextStyle,
                    maxLines = 1,

                )
                val desiredWidthPx = l.size.width
                val baseFontSizePx = mentionTextStyle.fontSize.value // In SP units
                val baseFontSizePxActual = baseFontSizePx * density

                // The width in 'em' units relative to the base font size
                val widthInEm = (desiredWidthPx / baseFontSizePxActual).em

                inlineTextContent(widthInEm, 1.6.em) {
                    Log.d("a", "$mt, $widthInEm")
                    Text(
                        "@${user.username}",
                        modifier = Modifier
                            .background(
                                Color(0x4428D3F3),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(top=1.dp,bottom=1.dp),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        style = mentionTextStyle
                    )
                }
            } else {
                inlineTextContent(8.em, 1.6.em) {
                    Log.d("a", "no")
                    Text(mt)
                }
            }
        }

        val a = content.richAnnotatedWith(listOf(mentionAnnotation, autoLinkAnnotation))

        Text(
            a.annotatedString,
            inlineContent = a.inlineContentMap,
            modifier = Modifier.padding(5.dp).padding(start=10.dp,top=5.dp),
        )

    }

}

@Preview
@Composable
fun PreviewMessage() {
    val d = Message(
        id = 1L,
        channelId = "1234567890",
        author = User(
            id = 1234567890L,
            username = "TestUser",
            discriminator = "0001",
            avatarHash = "abcdef1234567890abcdef1234567890",
            bot = false,
            globalName = "TestUserGlobal",
            avatarDecorationData = null,
            publicFlags = 0
        ),
        content = "Hello <@1234567890> <@1234567890>! How are you?",
        timestamp = "2023-10-01T12:00:00Z",
        editedTimestamp = null,
        tts = false,
        mentionEveryone = false,
        mentions = listOf(User(1234567890L, "MentionedUser", "0002", "abcdef1234567890abcdef1234567891", null, "MentionedUserGlobal", 0, false)),
        mentionRoles = emptyList(),
        mentionChannels = null,
        attachments = emptyList(),
        embeds = emptyList(),
        reactions = emptyList(),
        nonce = null,
        pinned = false,
        webhookId = null,
        type = 0,
        activity = null,
        application = null,
        applicationId = null,
        flags = 0,
        messageReference = null,
        messageSnapshots = emptyList(),
        referencedMessage = null,
        interactionMetadata = null,
        thread = null,
        components = emptyList(),
        stickerItems = emptyList(),
        stickers = emptyList(),
        position = 0,
        roleSubscriptionData = null,
        resolved = null,
        poll = null,
        call = null
    )
    d.UI()
}