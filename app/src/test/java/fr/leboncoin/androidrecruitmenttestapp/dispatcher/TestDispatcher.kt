package fr.leboncoin.androidrecruitmenttestapp.dispatcher

import fr.leboncoin.core.kotlin.coroutines.providers.DispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
val testDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())

val TestDispatcherProvider = object : DispatcherProvider {
    override val main: CoroutineContext = testDispatcher
    override val io: CoroutineContext = testDispatcher
    override val default: CoroutineContext = testDispatcher
    override val unconfined: CoroutineContext = testDispatcher
    override val limitedParallelism: CoroutineContext = testDispatcher
}
