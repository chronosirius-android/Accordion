package xyz.chronosirius.accordion.global_models

import xyz.chronosirius.accordion.data.DataObject

abstract class UserBase: Snowflaked() {
    abstract val username: String
    abstract val avatarHash: String?
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