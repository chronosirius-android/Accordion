package xyz.chronosirius.accordion

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicInteger

object LoadingStateManager {
    private val _activeRequests = AtomicInteger(0) // Atomic for thread safety
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    fun incrementActiveRequests() {
        if (_activeRequests.incrementAndGet() == 1) {
            _isLoading.value = true // First active request, show loading
        }
    }

    fun decrementActiveRequests() {
        if (_activeRequests.decrementAndGet() == 0) {
            _isLoading.value = false // Last active request, hide loading
        }
    }
}