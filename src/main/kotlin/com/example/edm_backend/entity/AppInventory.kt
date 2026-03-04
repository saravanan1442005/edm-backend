package com.example.edm_backend.entity


import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "app_inventory")
data class AppInventory(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    val device: Device,

    val appName: String,
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val installSource: String,
    val isSystemApp: Boolean,
    val collectedAt: LocalDateTime = LocalDateTime.now()
)