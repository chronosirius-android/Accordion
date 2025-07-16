package xyz.chronosirius.accordion.repositories

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import xyz.chronosirius.accordion.data.DataObject

class GatewayRepository {
    // This repository is used to manage the Discord Gateway connection
    // It will handle events, messages, and other interactions with the gateway

    // Future implementations will include methods to connect, disconnect, and handle events

    private val _latestMessage: MutableStateFlow<DataObject> = MutableStateFlow(DataObject.empty())
    val latestMessage = _latestMessage.asStateFlow()

    private val outgoingFrameQueue: ArrayDeque<DataObject> = ArrayDeque()

    init {

    }

    fun pushToApp(message: DataObject) {
        // Add a message to the repository
        _latestMessage.value = message
    }

    fun queueOutgoing(message: DataObject) {
        // Add a message to the outgoing queue
        outgoingFrameQueue.add(message)
    }




}