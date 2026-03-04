package com.example.edm_backend.controller

import com.example.edm_backend.dto.AppInventoryRequest
import com.example.edm_backend.entity.AppInventory
import com.example.edm_backend.repository.AppInventoryRepository
import com.example.edm_backend.repository.DeviceRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class AppInventoryController(
    private val deviceRepository: DeviceRepository,
    private val appInventoryRepository: AppInventoryRepository
) {

    @PostMapping("/app-inventory")
    fun saveAppInventory(@RequestBody request: AppInventoryRequest): ResponseEntity<Map<String, String>> {
        val device = deviceRepository.findByDeviceUuid(request.deviceUuid)
            ?: return ResponseEntity.status(404).body(mapOf("error" to "Device not found. Enroll first."))

        val apps = request.apps.map {
            AppInventory(
                device = device,
                appName = it.appName,
                packageName = it.packageName,
                versionName = it.versionName,
                versionCode = it.versionCode,
                installSource = it.installSource,
                isSystemApp = it.isSystemApp
            )
        }
        appInventoryRepository.saveAll(apps)
        return ResponseEntity.ok(mapOf("status" to "app inventory saved", "count" to apps.size.toString()))
    }
}