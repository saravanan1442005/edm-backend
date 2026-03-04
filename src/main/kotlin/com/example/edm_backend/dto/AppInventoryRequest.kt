package com.example.edm_backend.dto

data class AppInventoryRequest(
    val deviceUuid: String,
    val apps: List<AppInfoDto>
)

data class AppInfoDto(
    val appName: String,
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val installSource: String,
    val isSystemApp: Boolean
)