package com.example.edm_backend.dto

import com.example.edm_backend.entity.EnrollmentSource
import jakarta.validation.constraints.NotBlank

data class EnrollRequest(
    @field:NotBlank
    val deviceUuid: String,

    @field:NotBlank
    val enrollmentToken: String,

    val enrollmentSource: EnrollmentSource = EnrollmentSource.MANUAL
)