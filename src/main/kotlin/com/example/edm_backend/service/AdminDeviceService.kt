package com.example.edm_backend.service

import com.example.edm_backend.repository.AppInventoryRepository
import com.example.edm_backend.repository.DeviceCommandRepository
import com.example.edm_backend.repository.DeviceInfoRepository
import com.example.edm_backend.repository.DeviceRepository
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AdminDeviceService(
    private val deviceRepository: DeviceRepository,
    private val deviceInfoRepository: DeviceInfoRepository,
    private val appInventoryRepository: AppInventoryRepository,
    private val deviceCommandRepository: DeviceCommandRepository,
    private val entityManager: EntityManager
) {

    @Transactional
    fun deleteDeviceById(id: UUID): Boolean {
        if (!deviceRepository.existsById(id)) {
            return false
        }

        // Bulk delete dependent rows first to avoid FK constraint issues.
        deviceCommandRepository.deleteAllByDeviceId(id)
        appInventoryRepository.deleteAllByDeviceId(id)
        deviceInfoRepository.deleteAllByDeviceId(id)
        entityManager.flush()

        deviceRepository.deleteById(id)

        return true
    }
}
