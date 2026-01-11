package com.aarevalo.parking.authentication.domain.usecase

import com.aarevalo.parking.authentication.domain.repository.AuthRepository
import com.aarevalo.parking.core.domain.util.Resource
import javax.inject.Inject

/**
 * Use case for signing out the current user.
 */
class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Resource<Unit> {
        return authRepository.signOut()
    }
}
