package xyz.chronosirius.accordion.global_models

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import xyz.chronosirius.accordion.data.DataObject
import xyz.chronosirius.accordion.getDefaultAvatar

abstract class UserBase: Snowflaked() {
    abstract val username: String
    abstract val avatarHash: String?
    @Composable
    fun Avatar(modifier: Modifier = Modifier) {
        if (avatarHash != null) {
            AsyncImage(
                model = "https://cdn.discordapp.com/avatars/${id}/${avatarHash}.webp?size=512",
                contentDescription = "User Avatar",
                modifier = modifier,
            )
        } else {
            Image(
                painter = painterResource(getDefaultAvatar(id)),
                contentDescription = "User Avatar",
            )
        }
//        var image by rememberSaveable(saver = Saver<MutableState<Bitmap>, ByteArray>(
//            save = {
//                val outputStream = ByteArrayOutputStream()
//                it.value.compress(
//                    Bitmap.CompressFormat.WEBP_LOSSLESS,
//                    100, outputStream)
//                outputStream.toByteArray()
//            },
//            restore = {
//                mutableStateOf(BitmapFactory.decodeByteArray(it, 0, it.size))
//            }
//        )) { mutableStateOf(createBitmap(1,1)) }
//        LaunchedEffect(Unit) {
//            try {
//                if (avatarHash != null) {
//                    image = vm.getAvatarBitmap(id, avatarHash!!, ctx.cacheDir)
//                } else {
//                    image = BitmapFactory.decodeResource(ctx.resources, vm.getDefaultAvatar(id))
//                }
//            } catch (_: Exception) {
//                image = BitmapFactory.decodeResource(ctx.resources, vm.getDefaultAvatar(id))
//            }
//        }
//        Image(bitmap = image.asImageBitmap(), contentDescription = "User Avatar", modifier)
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
                da.getInt("public_flags", 0),
                da.getBoolean("bot")
            )
        }
    }
}