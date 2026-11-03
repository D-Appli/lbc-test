package fr.leboncoin.core.kotlin.coroutines.providers

import kotlin.coroutines.CoroutineContext

interface DispatcherProvider {
    val main: CoroutineContext
    val io: CoroutineContext
    val default: CoroutineContext
    val unconfined: CoroutineContext
    val limitedParallelism: CoroutineContext
}
