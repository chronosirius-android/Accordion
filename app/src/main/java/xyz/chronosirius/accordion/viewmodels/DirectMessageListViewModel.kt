package xyz.chronosirius.accordion.viewmodels

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
import javax.inject.Inject

data class DirectMessageListState(
    val channels: List<DMChannel> = emptyList()
)

@HiltViewModel
class DirectMessageListViewModel @Inject constructor(
    private val dmRepository: DirectMessagesRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(DirectMessageListState())
    val uiState = _uiState.asStateFlow()

    init {
        // Initialize the view model by loading direct messages
        getDMChannels()

        startCollection()
    }

    // update order of messages in the list
    private fun startCollection() {
        // This method can be used to start collecting updates from the repository
        // For example, you might want to listen for new messages or updates to existing channels
        // Currently, it does nothing but can be implemented later if needed
        viewModelScope.launch {
            DiscordGatewayService.latestMessage.collect { gwEvent ->
                // Check if the message is for the current channel
                if (gwEvent.getString("t", "") != "MESSAGE_CREATE") return@collect

                val message = gwEvent.getObject("d").let { Message.fromJson(it) }

                val q = _uiState.value.channels.toMutableList()

                val channelListChannelId = q.indexOfFirst { it.id == message.channelId.toLong() }
                if (channelListChannelId == -1) return@collect // If the channel is not found, do nothing
                val channel = q[channelListChannelId].copy(lastMessageId = message.id.toString())
                q.removeAt(channelListChannelId)
                q.add(0, channel)

                // Update the UI state with the new list of channels
                _uiState.value = _uiState.value.copy(channels = q)
            }
        }
    }

    fun getDMChannels() {
        viewModelScope.launch {
            // Fetch direct messages from the repository and update the UI state
            try {
                _uiState.value = _uiState.value.copy(channels = dmRepository.getDirectMessages().sortedWith(
                    compareByDescending { it.lastMessageTimeUnix() }
                ))
            } catch (_: Throwable) {
                // Handle any errors that occur during the fetch operation
                // For now, we just ignore the error
            }
        }
    }
}