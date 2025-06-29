package xyz.chronosirius.accordion.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import xyz.chronosirius.accordion.DiscordGatewayService
import xyz.chronosirius.accordion.global_models.DMChannel
import xyz.chronosirius.accordion.global_models.Message
import xyz.chronosirius.accordion.repositories.DirectMessagesRepository
import xyz.chronosirius.accordion.repositories.GatewayRepository
import javax.inject.Inject

data class DirectMessageConversationState(
    val channel: DMChannel = DMChannel(
        id = 0L,
        type = 0,
        lastMessageId = "",
        flags = 0,
        recipients = emptyList(),
        name = null,
        icon = null
    ),
    val messages: List<Message> = emptyList(),
)

@HiltViewModel
class DirectMessageConversationViewModel @Inject constructor(
    private val dmRepository: DirectMessagesRepository,
    private val gatewayRepository: GatewayRepository,
    private val ssh: SavedStateHandle
): ViewModel() {
    // This view model is used to manage the state of a direct message conversation
    // It fetches messages for a specific channel and provides methods to access them



    private val channelId: Long = ssh.get<String>("channelId")?.toLongOrNull() ?: throw IllegalStateException("no channel set in SavedStateHandle")


    private val _uiState = MutableStateFlow(DirectMessageConversationState())

    public var lastSentMessageId = 0L
        private set
    val uiState = _uiState.asStateFlow()

    init {
        // Initialize the view model by loading messages for the specified channel
        getChannel()
        getMessages()

        startCollection()

    }

    private fun startCollection() {
        viewModelScope.launch {
            // Collect messages from the gateway service
            DiscordGatewayService.latestMessage.collect { gwEvent ->
                // Check if the message is for the current channel
                if (gwEvent.getString("t", "") != "MESSAGE_CREATE") return@collect

                val message = gwEvent.getObject("d").let { Message.fromJson(it) }

                if (message.channelId == channelId.toString()) {
                    // Add the new message to the current state
                    _uiState.value = _uiState.value.copy(
                        messages = listOf(message) + _uiState.value.messages
                    )
                }
            }
        }
    }

    private fun getChannel() {
        viewModelScope.launch {
            // Fetch the channel from the repository using the channelId
            val channel = dmRepository.getChannelById(channelId)
            _uiState.value = _uiState.value.copy(channel = channel)
        }
    }

    private fun getMessages() {
        viewModelScope.launch {
            // Fetch messages for the channel from the repository
            val messages = dmRepository.getMessagesForChannel(channelId)
            _uiState.value = _uiState.value.copy(messages = messages.sortedWith(
                compareByDescending { it.timestamp }
            ))
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            // Send a message to the channel using the repository
            val q = dmRepository.sendMessage(channelId, text)
            // After sending, refresh the messages to include the new one
            //getMessages()
//            _uiState.value = _uiState.value.copy(
//                messages = listOf(q) + _uiState.value.messages
//            )

            lastSentMessageId = q.id.toLong()
        }
    }
}