package xyz.chronosirius.accordion.models

data class ResumeData(
    var url: String = "wss://gateway.discord.gg/?v=9&encoding=json",
    var sessionId: String = "",
    var seq: Int = 0,
    var shouldResume: Boolean = true
)