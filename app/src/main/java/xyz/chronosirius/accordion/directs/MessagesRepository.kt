package xyz.chronosirius.accordion.directs

import xyz.chronosirius.accordion.global_models.Message

class MessagesRepository(
    private val messagesRemoteDataSource: MessageDataSource,

) {
    // This class is used to hold data that needs to be shared between different parts of the app
    // It is a singleton class, meaning that only one instance of it will exist in the app
    // This is useful for storing data that needs to be accessed from different parts of the app
    // without having to pass it around as arguments
    companion object {
        // The data that needs to be shared
        var token: String = ""
    }
}