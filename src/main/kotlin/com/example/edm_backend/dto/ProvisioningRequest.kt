package com.example.edm_backend.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class ProvisioningRequest(
    @field:Min(1)
    @field:Max(500)
    val maxUses: Int = 1,

    @field:Min(1)
    @field:Max(720)
    val ttlHours: Long? = 24,

    val enrollmentUrl: String? = null,
    val notes: String? = null,

    // Android Enterprise Device Owner Provisioning specifics
    val apkUrl: String? = null,
    val apkChecksum: String? = null,
    val adminComponent: String? = null
)
