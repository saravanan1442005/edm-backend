package com.example.edm_backend.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "device_commands")
data class DeviceCommand(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    val device: Device,

    @Enumerated(EnumType.STRING)
    val command: CommandType,

    @Enumerated(EnumType.STRING)
    var status: CommandStatus = CommandStatus.PENDING,

    val createdAt: LocalDateTime = LocalDateTime.now(),
    var executedAt: LocalDateTime? = null
)

enum class CommandType {
    WIPE, LOCK, RESET_PASSWORD, DISABLE_CAMERA, ENABLE_CAMERA
}

enum class CommandStatus {
    PENDING, EXECUTED, FAILED
}