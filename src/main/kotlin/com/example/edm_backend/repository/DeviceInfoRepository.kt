package com.example.edm_backend.repository

import com.example.edm_backend.entity.Device
import com.example.edm_backend.entity.DeviceInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

interface DeviceInfoRepository : JpaRepository<DeviceInfo, UUID> {
    fun findByDevice(device: Device): List<DeviceInfo>

    @Transactional
    fun deleteByDevice(device: Device)

    @Modifying
    @Transactional
    @Query("delete from DeviceInfo d where d.device.id = :deviceId")
    fun deleteAllByDeviceId(deviceId: UUID): Int
}