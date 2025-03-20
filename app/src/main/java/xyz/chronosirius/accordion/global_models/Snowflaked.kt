package xyz.chronosirius.accordion.global_models

abstract class Snowflaked {
    abstract val id: Long

    fun getTimestamp(): Long {
        return (id.toLong() shr 22) + 1420070400000
    }
}