package xyz.chronosirius.accordion.models

data class ResumeData(
    var url: String = "",
    var sessionId: String = "",
    var seq: Int = 0,
    var shouldResume: Boolean = false
)