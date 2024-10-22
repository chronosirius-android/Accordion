package xyz.chronosirius.accordion.global_models

abstract class Snowflaked {
    abstract val id: Int

    fun getTimestamp(): Long {
        return (id shr 22) + 1420070400000
    }
}