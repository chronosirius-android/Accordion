package xyz.chronosirius.accordion.global_models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap
import xyz.chronosirius.accordion.data.DataObject
import xyz.chronosirius.accordion.viewmodels.AccordionViewModel
import java.io.ByteArrayOutputStream
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

    @Composable
    fun Icon(vm: AccordionViewModel, ctx: Context, modifier: Modifier) {
        var image by rememberSaveable(saver = Saver<MutableState<Bitmap>, ByteArray>(
            save = {
                    val outputStream = ByteArrayOutputStream()
                    it.value.compress(
                        Bitmap.CompressFormat.WEBP_LOSSLESS,
                        100, outputStream)
                    outputStream.toByteArray()
                   },
            restore = {
                mutableStateOf(BitmapFactory.decodeByteArray(it, 0, it.size))
            }
        )) { mutableStateOf(createBitmap(1,1)) }
        Log.d("DMChannel", "Icon: $icon")
        LaunchedEffect(Unit) {
            try {
                if (icon != null) {
                    // Load the image from the URL
                    image = vm.getImageBitmap("channel-icons", id, icon, ctx.cacheDir)
                } else {
                    // Load the default icon
                    if (recipients.isNotEmpty()) {
                        if (recipients[0].avatarHash != null) {
                            image = vm.getAvatarBitmap(recipients[0].id, recipients[0].avatarHash!!, ctx.cacheDir)
                        } else {
                            image = BitmapFactory.decodeResource(ctx.resources, vm.getDefaultAvatar(recipients[0].id))
                        }
                    }
                }
            } catch(it: Exception) {
                Log.e("DMChannel", "Error loading image: $it")
                throw it
                image = BitmapFactory.decodeResource(ctx.resources, vm.getDefaultAvatar(recipients[0].id))
            }
        }

        Image(bitmap = image.asImageBitmap(), contentDescription = "Channel Icon", modifier)
    }
}