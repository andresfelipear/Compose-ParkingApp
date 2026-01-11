package com.aarevalo.parking.core.domain.util

/**
 * Sealed class representing network errors that can occur during API calls.
 */
sealed class NetworkError : Exception() {
    data object NoInternet : NetworkError() {
        private fun readResolve(): Any = NoInternet
        override val message: String = "No internet connection available"
    }

    data object Timeout : NetworkError() {
        private fun readResolve(): Any = Timeout
        override val message: String = "Request timed out"
    }

    data object ServerError : NetworkError() {
        private fun readResolve(): Any = ServerError
        override val message: String = "Server error occurred"
    }

    data object NotFound : NetworkError() {
        private fun readResolve(): Any = NotFound
        override val message: String = "Resource not found"
    }

    data object Unauthorized : NetworkError() {
        private fun readResolve(): Any = Unauthorized
        override val message: String = "Unauthorized access"
    }

    data class Unknown(override val message: String = "Unknown error occurred") : NetworkError()
}
