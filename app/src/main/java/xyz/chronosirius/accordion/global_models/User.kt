package xyz.chronosirius.accordion.global_models

class User(
    val id: String,
    val username: String,
    val globalName: String,
    val avatarHash: String,
    val avatarDecorationData: Any?,
    val discriminator: String = "0",
    val publicFlags: Int,
    val clan: Any?,
    val bot: Boolean
) {
}