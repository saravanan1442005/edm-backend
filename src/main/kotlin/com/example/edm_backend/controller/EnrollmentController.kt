package com.example.edm_backend.controller

import com.example.edm_backend.dto.EnrollRequest
import com.example.edm_backend.entity.Device
import com.example.edm_backend.repository.DeviceRepository
import com.example.edm_backend.service.EnrollmentTokenException
import com.example.edm_backend.service.EnrollmentTokenService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class EnrollmentController(
    private val deviceRepository: DeviceRepository,
    private val enrollmentTokenService: EnrollmentTokenService
) {

    @PostMapping("/enroll")
    fun enroll(@Valid @RequestBody request: EnrollRequest): ResponseEntity<Map<String, String>> {

        // Restrict duplicate enrollment
        val existing = deviceRepository.findByDeviceUuid(request.deviceUuid)
        if (existing != null) {
            return ResponseEntity.ok(mapOf(
                "status" to "ALREADY_ENROLLED",
                "deviceId" to existing.id.toString(),
                "message" to "Device already enrolled — duplicate request ignored"
            ))
        }

        val token = try {
            enrollmentTokenService.validateAndConsume(request.enrollmentToken)
        } catch (ex: EnrollmentTokenException) {
            return ResponseEntity.badRequest().body(
                mapOf(
                    "status" to ex.code.name,
                    "message" to ex.message
                )
            )
        }

        // New enrollment
        val device = deviceRepository.save(
            Device(
                deviceUuid = request.deviceUuid,
                enrollmentToken = token.tokenHint,
                enrollmentTokenId = token.id,
                enrollmentSource = request.enrollmentSource
            )
        )
        return ResponseEntity.status(201).body(mapOf(
            "status" to "ENROLLED",
            "deviceId" to device.id.toString(),
            "message" to "Device enrolled successfully",
            "enrollmentSource" to request.enrollmentSource.name
        ))
    }
}