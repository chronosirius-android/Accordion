package xyz.chronosirius.accordion.global_models

import xyz.chronosirius.accordion.data.DataObject

abstract class UserBase: Snowflaked() {
    abstract val username: String
    abstract val avatarHash: String
}

class User(
    override val id: String,
    override val username: String,
    val globalName: String,
    override val avatarHash: String,
    val avatarDecorationData: DataObject?,
    val discriminator: String = "0",
    val publicFlags: Int,
    val clan: Any?,
    val bot: Boolean
): UserBase() {
}