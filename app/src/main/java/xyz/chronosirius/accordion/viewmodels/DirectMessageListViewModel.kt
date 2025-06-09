package xyz.chronosirius.accordion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import xyz.chronosirius.accordion.global_models.DMChannel
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