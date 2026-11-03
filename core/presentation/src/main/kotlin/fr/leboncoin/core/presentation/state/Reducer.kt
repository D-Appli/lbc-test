package fr.leboncoin.core.presentation.state

import kotlinx.coroutines.flow.StateFlow

interface Reducer<S : State, A : Action> {

    val stateFlow: StateFlow<S>

    suspend fun update(action: A)
}
