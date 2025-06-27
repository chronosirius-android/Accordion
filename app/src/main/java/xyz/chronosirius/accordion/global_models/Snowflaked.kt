package xyz.chronosirius.accordion.global_models

abstract class Snowflaked {
    abstract val id: Long

    /*
     * Discord's snowflake IDs are 64-bit integers where the first 42 bits represent the timestamp in milliseconds
     * The timestamp is calculated by shifting the ID right by 22 bits and adding a base timestamp of 1420070400000 (which corresponds to January 1, 2015).
     * Returns the timestamp in milliseconds.
     */
    fun getTimestamp(): Long {

        return (id shr 22) + 1420070400000
    }
}