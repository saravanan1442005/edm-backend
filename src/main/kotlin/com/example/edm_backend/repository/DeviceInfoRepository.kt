package com.example.edm_backend.repository

import com.example.edm_backend.entity.Device
import com.example.edm_backend.entity.DeviceInfo
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DeviceInfoRepository : JpaRepository<DeviceInfo, UUID> {
    fun findByDevice(device: Device): List<DeviceInfo>
}