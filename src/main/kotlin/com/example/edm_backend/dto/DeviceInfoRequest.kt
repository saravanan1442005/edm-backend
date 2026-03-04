package com.example.edm_backend.dto


data class DeviceInfoRequest(
    val deviceUuid: String,
    val model: String,
    val manufacturer: String,
    val osVersion: String,
    val sdkVersion: Int,
    val serialNumber: String,
    val imei: String? = null
)