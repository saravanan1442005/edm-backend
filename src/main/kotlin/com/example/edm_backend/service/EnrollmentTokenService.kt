package com.example.edm_backend.service

import com.example.edm_backend.entity.EnrollmentToken
import com.example.edm_backend.entity.EnrollmentTokenStatus
import com.example.edm_backend.repository.EnrollmentTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.LocalDateTime
import java.util.UUID

@Service
class EnrollmentTokenService(
    private val enrollmentTokenRepository: EnrollmentTokenRepository
) {
    private val random = SecureRandom()

    @Transactional
    fun createToken(maxUses: Int, ttlHours: Long?, notes: String?): IssuedToken {
        val normalizedMaxUses = maxUses.coerceAtLeast(1)
        val rawToken = generateRawToken()
        val tokenHash = sha256(rawToken)
        val tokenHint = buildTokenHint(rawToken)
        val expiresAt = ttlHours?.let { LocalDateTime.now().plusHours(it) }

        val token = enrollmentTokenRepository.save(
            EnrollmentToken(
                tokenHash = tokenHash,
                tokenHint = tokenHint,
                maxUses = normalizedMaxUses,
                expiresAt = expiresAt,
                notes = notes
            )
        )

        return IssuedToken(token = token, rawToken = rawToken)
    }

    @Transactional
    fun validateAndConsume(rawToken: String): EnrollmentToken {
        val tokenHash = sha256(rawToken)
        val token = enrollmentTokenRepository.findByTokenHash(tokenHash)
            ?: throw EnrollmentTokenException(TokenErrorCode.INVALID_TOKEN, "Token is invalid")

        val now = LocalDateTime.now()

        if (token.status == EnrollmentTokenStatus.REVOKED) {
            throw EnrollmentTokenException(TokenErrorCode.TOKEN_REVOKED, "Token has been revoked")
        }

        if (token.expiresAt != null && now.isAfter(token.expiresAt)) {
            token.status = EnrollmentTokenStatus.EXPIRED
            enrollmentTokenRepository.save(token)
            throw EnrollmentTokenException(TokenErrorCode.TOKEN_EXPIRED, "Token has expired")
        }

        if (token.usedCount >= token.maxUses) {
            throw EnrollmentTokenException(TokenErrorCode.TOKEN_EXHAUSTED, "Token usage limit reached")
        }

        token.usedCount += 1
        return enrollmentTokenRepository.save(token)
    }

    @Transactional(readOnly = true)
    fun listTokens(): List<EnrollmentToken> = enrollmentTokenRepository.findAllByOrderByCreatedAtDesc()

    @Transactional
    fun revoke(id: UUID): EnrollmentToken {
        val token = enrollmentTokenRepository.findById(id)
            .orElseThrow { EnrollmentTokenException(TokenErrorCode.INVALID_TOKEN, "Token not found") }

        token.status = EnrollmentTokenStatus.REVOKED
        return enrollmentTokenRepository.save(token)
    }

    private fun generateRawToken(): String {
        val bytes = ByteArray(18)
        random.nextBytes(bytes)
        val hex = bytes.joinToString("") { "%02x".format(it) }
        return "EDM-$hex"
    }

    private fun buildTokenHint(rawToken: String): String {
        val start = rawToken.take(7)
        val end = rawToken.takeLast(4)
        return "$start...$end"
    }

    private fun sha256(value: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(value.toByteArray(StandardCharsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }
}

data class IssuedToken(
    val token: EnrollmentToken,
    val rawToken: String
)

enum class TokenErrorCode {
    INVALID_TOKEN,
    TOKEN_REVOKED,
    TOKEN_EXPIRED,
    TOKEN_EXHAUSTED
}

class EnrollmentTokenException(
    val code: TokenErrorCode,
    override val message: String
) : RuntimeException(message)
