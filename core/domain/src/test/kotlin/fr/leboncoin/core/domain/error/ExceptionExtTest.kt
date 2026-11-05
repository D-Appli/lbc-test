package fr.leboncoin.core.domain.error

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ExceptionExtTest {

    @Test
    fun `an unresolved host is an offline error`() {
        assertTrue(UnknownHostException("offline").isOfflineError())
    }

    @Test
    fun `an unreachable host is an offline error`() {
        assertTrue(NoRouteToHostException("offline").isOfflineError())
    }

    @Test
    fun `a refused connection is not an offline error`() {
        assertFalse(ConnectException("connection refused").isOfflineError())
    }

    @Test
    fun `a timeout is not an offline error`() {
        assertFalse(SocketTimeoutException("slow server").isOfflineError())
    }

    @Test
    fun `another network failure is not an offline error`() {
        assertFalse(IOException("server error").isOfflineError())
    }

    @Test
    fun `a failure unrelated to the network is not an offline error`() {
        assertFalse(IllegalStateException("bug").isOfflineError())
    }
}
