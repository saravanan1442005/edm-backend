package com.example.edm_backend.dto

data class EnrollRequest(
    val deviceUuid: String,
    val enrollmentToken: String
)