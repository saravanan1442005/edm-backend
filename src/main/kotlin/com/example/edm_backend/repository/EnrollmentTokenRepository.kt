package com.example.edm_backend.repository

import com.example.edm_backend.entity.EnrollmentToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface EnrollmentTokenRepository : JpaRepository<EnrollmentToken, UUID> {
    fun findByTokenHash(tokenHash: String): EnrollmentToken?
    fun findAllByOrderByCreatedAtDesc(): List<EnrollmentToken>
}
