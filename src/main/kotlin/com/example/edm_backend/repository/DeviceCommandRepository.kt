package com.example.edm_backend.repository

import com.example.edm_backend.entity.Device
import com.example.edm_backend.entity.DeviceCommand
import com.example.edm_backend.entity.CommandStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

interface DeviceCommandRepository : JpaRepository<DeviceCommand, UUID> {
    fun findByDeviceAndStatus(device: Device, status: CommandStatus): List<DeviceCommand>

    @Transactional
    fun deleteByDevice(device: Device)

    @Modifying
    @Transactional
    @Query("delete from DeviceCommand c where c.device.id = :deviceId")
    fun deleteAllByDeviceId(deviceId: UUID): Int
}