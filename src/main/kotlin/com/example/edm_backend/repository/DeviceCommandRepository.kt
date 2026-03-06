package com.example.edm_backend.repository

import com.example.edm_backend.entity.Device
import com.example.edm_backend.entity.DeviceCommand
import com.example.edm_backend.entity.CommandStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DeviceCommandRepository : JpaRepository<DeviceCommand, UUID> {
    fun findByDeviceAndStatus(device: Device, status: CommandStatus): List<DeviceCommand>
}