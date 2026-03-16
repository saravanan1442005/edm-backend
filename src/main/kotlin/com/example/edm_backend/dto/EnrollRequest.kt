package com.example.edm_backend.dto

import com.example.edm_backend.entity.EnrollmentSource

data class EnrollRequest(
    val deviceUuid: String,

    val enrollmentToken: String,

    val enrollmentSource: EnrollmentSource = EnrollmentSource.MANUAL
)