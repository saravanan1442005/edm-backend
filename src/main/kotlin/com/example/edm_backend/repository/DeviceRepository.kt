package com.example.edm_backend.repository


import com.example.edm_backend.entity.Device
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DeviceRepository : JpaRepository<Device, UUID> {
    fun findByDeviceUuid(deviceUuid: String): Device?
}