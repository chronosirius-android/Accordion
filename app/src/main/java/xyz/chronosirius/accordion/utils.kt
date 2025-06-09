package xyz.chronosirius.accordion

import androidx.annotation.DrawableRes

@DrawableRes
fun getDefaultAvatar(userId: Long): Int {
    // This will return a default avatar for the user
    // based on their user ID
    var defaultAvatarIndex = (userId shr 22) % 6

    if (defaultAvatarIndex < 0) {
        defaultAvatarIndex = -defaultAvatarIndex
    }


    return when (defaultAvatarIndex) {
        0L -> R.drawable.default_avatar_0
        1L -> R.drawable.default_avatar_1
        2L -> R.drawable.default_avatar_2
        3L -> R.drawable.default_avatar_3
        4L -> R.drawable.default_avatar_4
        5L -> R.drawable.default_avatar_5
        else -> throw IllegalArgumentException("Invalid default avatar index: $defaultAvatarIndex")
    }
}