package xyz.chronosirius.accordion.global_models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import xyz.chronosirius.accordion.data.DataObject
import xyz.chronosirius.accordion.viewmodels.AccordionViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap
import java.io.ByteArrayOutputStream

abstract class UserBase: Snowflaked() {
    abstract val username: String
    abstract val avatarHash: String?
    @Composable
    fun Avatar(vm: AccordionViewModel, ctx: Context, modifier: Modifier = Modifier) {
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
        LaunchedEffect(Unit) {
            try {
                if (avatarHash != null) {
                    image = vm.getAvatarBitmap(id, avatarHash!!, ctx.cacheDir)
                } else {
                    image = BitmapFactory.decodeResource(ctx.resources, vm.getDefaultAvatar(id))
                }
            } catch (_: Exception) {
                image = BitmapFactory.decodeResource(ctx.resources, vm.getDefaultAvatar(id))
            }
        }
        Image(bitmap = image.asImageBitmap(), contentDescription = "User Avatar", modifier)
    }
}

class User(
    override val id: Long,
    override val username: String,
    val globalName: String?,
    override val avatarHash: String?,
    val avatarDecorationData: DataObject?,
    val discriminator: String = "0",
    val publicFlags: Int,
    val bot: Boolean
): UserBase() {
    companion object {
        fun fromJson(da: DataObject): User {
            return User(
                da.getString("id").toLong(),
                da.getString("username"),
                da.getString("global_name", null),
                da.getString("avatar", null),
                da.getObject("avatar_decoration_data", null),
                da.getString("discriminator"),
                da.getInt("public_flags"),
                da.getBoolean("bot")
            )
        }
    }
}