package com.example.edm_backend.controller

import com.example.edm_backend.dto.DeviceInfoRequest
import com.example.edm_backend.entity.DeviceInfo
import com.example.edm_backend.repository.DeviceInfoRepository
import com.example.edm_backend.repository.DeviceRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class DeviceInfoController(
    private val deviceRepository: DeviceRepository,
    private val deviceInfoRepository: DeviceInfoRepository
) {

    @PostMapping("/device-info")
    fun saveDeviceInfo(@RequestBody request: DeviceInfoRequest): ResponseEntity<Map<String, String>> {
        val device = deviceRepository.findByDeviceUuid(request.deviceUuid)
            ?: return ResponseEntity.status(404).body(mapOf("error" to "Device not found. Enroll first."))

        deviceInfoRepository.save(
            DeviceInfo(
                device = device,
                model = request.model,
                manufacturer = request.manufacturer,
                osVersion = request.osVersion,
                sdkVersion = request.sdkVersion,
                serialNumber = request.serialNumber,
                deviceUuid = request.deviceUuid,
                imei = request.imei
            )
        )
        return ResponseEntity.ok(mapOf("status" to "device info saved"))
    }
}