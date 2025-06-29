package xyz.chronosirius.accordion.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import xyz.chronosirius.accordion.global_models.Guild
import xyz.chronosirius.accordion.global_models.GuildUIFolder
import xyz.chronosirius.accordion.repositories.GuildRepository
import xyz.chronosirius.accordion.repositories.RawGuildFolder
import xyz.chronosirius.accordion.repositories.SettingsRepository
import java.lang.IllegalStateException
import javax.inject.Inject

data class ServerListUiState(
    @Deprecated("use folders") val guilds: List<Guild> = emptyList(), // This will hold the list of server IDs
    val folders: List<GuildUIFolder> = emptyList()
)
@HiltViewModel
class ServerListViewModel @Inject constructor(
    val guildRepository: GuildRepository,
    val settingsRepository: SettingsRepository
): ViewModel() {
    // This view model is used to manage the state of the server list
    // It fetches servers from the repository and provides methods to access them

    private val _uiState = MutableStateFlow(ServerListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Initialize the view model by loading servers
        // This can be done by calling a method on the serverRepository
        // For example: serverRepository.loadServers()
        loadServers()
    }

    fun loadServers() {
        viewModelScope.launch {
            // Call the repository to load servers
            // serverRepository.loadServers() // Uncomment when the method is implemented
            val guilds = guildRepository.getGuilds()
            val order = settingsRepository.getGuildFolders()



            _uiState.value = _uiState.value.copy(
                guilds = guilds, // Update the UI state with the loaded guilds
                folders = constructUIFolders(guilds, order)
            )
        }
    }

    private fun constructUIFolders(guilds: List<Guild>, order: List<RawGuildFolder>): List<GuildUIFolder> {
        val uiFolders = mutableListOf<GuildUIFolder>()
        for (rgf in order) {
            uiFolders += GuildUIFolder(
                id = rgf.id,
                name = rgf.name,
                color = rgf.color,
                guilds = rgf.guildIds.map { id ->
                    guilds.find {
                        it.id.toString() == id
                    } ?: throw IllegalStateException("guild not found in guilds but in order (???)")
                }
            )
        }
        return uiFolders
    }

    // Additional methods to interact with the serverRepository can be added here
}