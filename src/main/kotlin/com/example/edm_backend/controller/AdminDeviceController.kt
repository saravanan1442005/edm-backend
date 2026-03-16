package com.example.edm_backend.controller

import com.example.edm_backend.service.AdminDeviceService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/admin/devices")
class AdminDeviceController(
    private val adminDeviceService: AdminDeviceService
) {
    private val logger = LoggerFactory.getLogger(AdminDeviceController::class.java)

    @DeleteMapping("/{id}")
    fun deleteDevice(@PathVariable id: UUID): ResponseEntity<Map<String, String>> {
        return deleteDeviceInternal(id)
    }

    // Fallback for clients/proxies where DELETE may be blocked.
    @PostMapping("/{id}/delete")
    fun deleteDevicePost(@PathVariable id: UUID): ResponseEntity<Map<String, String>> {
        return deleteDeviceInternal(id)
    }

    private fun deleteDeviceInternal(id: UUID): ResponseEntity<Map<String, String>> {
        val deleted = try {
            adminDeviceService.deleteDeviceById(id)
        } catch (ex: Exception) {
            logger.error("Failed to delete device with id={}", id, ex)
            return ResponseEntity.status(409).body(
                mapOf(
                    "status" to "DELETE_FAILED",
                    "message" to ((ex.javaClass.simpleName + ": " + (ex.message ?: "Failed to delete device")))
                )
            )
        }

        if (!deleted) {
            return ResponseEntity.status(404).body(
                mapOf(
                    "status" to "NOT_FOUND",
                    "message" to "Device not found"
                )
            )
        }

        return ResponseEntity.ok(
            mapOf(
                "status" to "DELETED",
                "message" to "Device removed successfully"
            )
        )
    }
}
