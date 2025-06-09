package xyz.chronosirius.accordion.global_models

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import xyz.chronosirius.accordion.data.DataObject
import xyz.chronosirius.accordion.getDefaultAvatar
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class DMChannel(
    override val id: Long,
    val type: Int,
    val lastMessageId: String,
    val flags: Int,
    val recipients: List<User>,
    val name: String?,
    val icon: String?
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
                da.getString("name", null),
                da.getString("icon", null)
            )
        }

    }

    fun sendMessage(message: Message) {
        // This will send a message to the channel

    }

    fun name(): String {
        return try {
            name ?: recipients[0].username
        } catch (_: Exception) {
            id.toString()
        }
    }

    fun lastMessageTimeUnix(): Long {
        return (lastMessageId.toLong() shr 22) + 1420070400000
    }

    fun timeSinceLastMessage(): DateTimePeriod {
        return (System.currentTimeMillis() - lastMessageTimeUnix()).toDuration(DurationUnit.MILLISECONDS)
            .toDateTimePeriod(Instant.fromEpochMilliseconds(lastMessageTimeUnix()), TimeZone.currentSystemDefault())
    }

    @Composable
    fun Icon(modifier: Modifier) {

        if (icon != null) {
            AsyncImage(
                model = "https://cdn.discordapp.com/channel-icons/$id/$icon.webp",
                contentDescription = "Channel Icon",
                error = painterResource(getDefaultAvatar(id)),
                placeholder = painterResource(getDefaultAvatar(id)),
                modifier = modifier,
            )
        } else if (recipients.isNotEmpty()) {
            if (recipients[0].avatarHash != null) {
                AsyncImage(
                    model = "https://cdn.discordapp.com/avatars/${recipients[0].id}/${recipients[0].avatarHash}.webp?size=480",
                    contentDescription = "User Avatar",
                    error = painterResource(getDefaultAvatar(recipients[0].id)),
                    placeholder = painterResource(getDefaultAvatar(recipients.first().id)),
                    modifier = modifier,
                )
            } else {
                Image(
                    painter = painterResource(getDefaultAvatar(recipients[0].id)),
                    contentDescription = "User Avatar",
                    modifier = modifier,
                )
            }
        } else {
            Image(
                painter = painterResource(getDefaultAvatar(id)),
                contentDescription = "Channel Icon",
                modifier = modifier,
            )
        }
//        var image by rememberSaveable(saver = Saver<MutableState<Bitmap>, ByteArray>(
//            save = {
//                    val outputStream = ByteArrayOutputStream()
//                    it.value.compress(
//                        Bitmap.CompressFormat.WEBP_LOSSLESS,
//                        100, outputStream)
//                    outputStream.toByteArray()
//                   },
//            restore = {
//                mutableStateOf(BitmapFactory.decodeByteArray(it, 0, it.size))
//            }
//        )) { mutableStateOf(createBitmap(1,1)) }
//        Log.d("DMChannel", "Icon: $icon")
//        LaunchedEffect(Unit) {
//            try {
//                if (icon != null) {
//                    // Load the image from the URL
//                    //image = vm.getImageBitmap("channel-icons", id, icon, ctx.cacheDir)
//                } else {
//                    // Load the default icon
//                    if (recipients.isNotEmpty()) {
//                        if (recipients[0].avatarHash != null) {
//                            image = vm.getAvatarBitmap(recipients[0].id, recipients[0].avatarHash!!, ctx.cacheDir)
//                        } else {
//                            image = BitmapFactory.decodeResource(ctx.resources, vm.getDefaultAvatar(recipients[0].id))
//                        }
//                    }
//                }
//            } catch(it: Exception) {
//                Log.e("DMChannel", "Error loading image: $it")
//                throw it
//                image = BitmapFactory.decodeResource(ctx.resources, vm.getDefaultAvatar(recipients[0].id))
//            }
//        }
//
//        Image(bitmap = image.asImageBitmap(), contentDescription = "Channel Icon", modifier)
    }
}

fun Duration.toDateTimePeriod(instant: Instant, timeZone: TimeZone): DateTimePeriod {
    return instant.periodUntil(instant + this, timeZone)
}