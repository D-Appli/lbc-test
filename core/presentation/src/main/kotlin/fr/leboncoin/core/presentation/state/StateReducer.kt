package fr.leboncoin.core.presentation.state

import fr.leboncoin.core.kotlin.coroutines.providers.DispatcherProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Used as a base class for UI reducer implementation.
 *
 * @param initialState Sets an initial state, as it is immutable for child classes
 * @param synchronizeUpdates Determines will calls to [update] methods be synchronised
 * @param dispatcherProvider Required for executing state update on a default dispatcher
 */
abstract class StateReducer<STATE : State, ACTION : Action>(
    initialState: STATE,
    private val dispatcherProvider: DispatcherProvider,
    private val synchronizeUpdates: Boolean = false,
) : Reducer<STATE, ACTION> {

    private val _stateFlow = MutableStateFlow(initialState)
    private val mutex by lazy { Mutex() }

    private suspend fun mutexAction(action: suspend () -> Unit) {
        if (synchronizeUpdates) {
            mutex.withLock { action() }
        } else {
            action()
        }
    }

    final override val stateFlow: StateFlow<STATE>
        get() = _stateFlow

    final override suspend fun update(action: ACTION) {
        mutexAction {
            withContext(dispatcherProvider.default) {
                val newState = reduce(action, stateFlow.value)
                _stateFlow.value = newState
            }
        }
    }

    /**
     * Prefer usage of [currentState] instead of [stateFlow] value while needed for state updates.
     */
    protected abstract fun reduce(action: ACTION, currentState: STATE): STATE
}
