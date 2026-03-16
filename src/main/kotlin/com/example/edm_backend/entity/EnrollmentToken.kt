package com.example.edm_backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "enrollment_tokens")
data class EnrollmentToken(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false, unique = true, length = 64)
    val tokenHash: String,

    @Column(nullable = false)
    val tokenHint: String,

    @Column(nullable = false)
    val maxUses: Int = 1,

    @Column(nullable = false)
    var usedCount: Int = 0,

    val expiresAt: LocalDateTime? = null,

    @Enumerated(EnumType.STRING)
    var status: EnrollmentTokenStatus = EnrollmentTokenStatus.ACTIVE,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    val notes: String? = null
)

enum class EnrollmentTokenStatus {
    ACTIVE,
    REVOKED,
    EXPIRED
}
