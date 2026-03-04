package com.example.edm_backend.entity


import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "devices")
data class Device(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false, unique = true)
    val deviceUuid: String,

    @Column(nullable = false)
    val enrollmentToken: String,

    val enrolledAt: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    val status: DeviceStatus = DeviceStatus.ENROLLED
)

enum class DeviceStatus { ENROLLED, INACTIVE }