package com.example.edm_backend.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class CreateEnrollmentTokenRequest(
    @field:Min(1)
    @field:Max(500)
    val maxUses: Int = 1,

    @field:Min(1)
    @field:Max(720)
    val ttlHours: Long? = 72,

    val notes: String? = null
)
