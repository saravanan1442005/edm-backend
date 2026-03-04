package com.example.edm_backend.repository

import com.example.edm_backend.entity.AppInventory
import com.example.edm_backend.entity.Device
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AppInventoryRepository : JpaRepository<AppInventory, UUID> {
    fun findByDevice(device: Device): List<AppInventory>
}