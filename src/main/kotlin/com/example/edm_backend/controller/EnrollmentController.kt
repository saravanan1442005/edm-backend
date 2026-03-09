package com.example.edm_backend.controller

import com.example.edm_backend.dto.EnrollRequest
import com.example.edm_backend.entity.Device
import com.example.edm_backend.repository.DeviceRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class EnrollmentController(private val deviceRepository: DeviceRepository) {

    @PostMapping("/enroll")
    fun enroll(@RequestBody request: EnrollRequest): ResponseEntity<Map<String, String>> {

        // Restrict duplicate enrollment
        val existing = deviceRepository.findByDeviceUuid(request.deviceUuid)
        if (existing != null) {
            return ResponseEntity.ok(mapOf(
                "status" to "ALREADY_ENROLLED",
                "deviceId" to existing.id.toString(),
                "message" to "Device already enrolled — duplicate request ignored"
            ))
        }

        // New enrollment
        val device = deviceRepository.save(
            Device(
                deviceUuid = request.deviceUuid,
                enrollmentToken = request.enrollmentToken
            )
        )
        return ResponseEntity.status(201).body(mapOf(
            "status" to "ENROLLED",
            "deviceId" to device.id.toString(),
            "message" to "Device enrolled successfully"
        ))
    }
}