package fr.leboncoin.core.domain.error

import java.net.NoRouteToHostException
import java.net.UnknownHostException

/** Whether this failure means that the device could not reach the network, e.g. in airplane mode. */
fun Exception.isOfflineError(): Boolean =
    this is UnknownHostException || this is NoRouteToHostException
