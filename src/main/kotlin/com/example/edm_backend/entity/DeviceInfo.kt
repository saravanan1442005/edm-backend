package com.example.edm_backend.entity


import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "device_info")
data class DeviceInfo(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    val device: Device,

    val model: String,
    val manufacturer: String,
    val osVersion: String,
    val sdkVersion: Int,
    val serialNumber: String,
    val deviceUuid: String,
    val imei: String? = null,
    val collectedAt: LocalDateTime = LocalDateTime.now()
)