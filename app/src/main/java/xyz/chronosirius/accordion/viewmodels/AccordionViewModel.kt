package xyz.chronosirius.accordion.viewmodels


import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import xyz.chronosirius.accordion.global_models.DMChannel
import xyz.chronosirius.accordion.global_models.Message

class AccordionViewModel: ViewModel() {
    // This class is used to hold data that needs to be shared between different parts of the app
    // It is a singleton class, meaning that only one instance of it will exist in the app
    // This is useful for storing data that needs to be accessed from different parts of the app
    // without having to pass it around as arguments

    // class is suffering "slow death" (deleting code slowly)
    private val _error: MutableStateFlow<Throwable?> = MutableStateFlow(null);
    val error = _error.asStateFlow()
    private val _isPulling = MutableStateFlow<Boolean>(false)
    val isPulling = _isPulling.asStateFlow()

}