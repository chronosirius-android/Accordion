package xyz.chronosirius.accordion.global_models

abstract class Snowflaked {
    abstract val id: String

    fun getTimestamp(): Long {
        return (id.toInt() shr 22) + 1420070400000
    }
}