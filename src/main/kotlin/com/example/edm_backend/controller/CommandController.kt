package com.example.edm_backend.controller

import com.example.edm_backend.entity.CommandStatus
import com.example.edm_backend.entity.CommandType
import com.example.edm_backend.entity.DeviceCommand
import com.example.edm_backend.repository.DeviceCommandRepository
import com.example.edm_backend.repository.DeviceRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.UUID

@RestController
@RequestMapping("/api")
class CommandController(
    private val deviceRepository: DeviceRepository,
    private val deviceCommandRepository: DeviceCommandRepository
) {

    // Android app polls this to get pending commands
    @GetMapping("/commands/{deviceUuid}")
    fun getPendingCommands(@PathVariable deviceUuid: String): ResponseEntity<List<Map<String, String>>> {
        val device = deviceRepository.findByDeviceUuid(deviceUuid)
            ?: return ResponseEntity.status(404).body(emptyList())

        val commands = deviceCommandRepository.findByDeviceAndStatus(device, CommandStatus.PENDING)
        val result = commands.map {
            mapOf(
                "id" to it.id.toString(),
                "command" to it.command.name
            )
        }
        return ResponseEntity.ok(result)
    }

    // Android app calls this after executing command
    @PostMapping("/commands/{id}/executed")
    fun markExecuted(@PathVariable id: UUID): ResponseEntity<Map<String, String>> {
        val command = deviceCommandRepository.findById(id).orElse(null)
            ?: return ResponseEntity.status(404).body(mapOf("error" to "Command not found"))

        val updated = command.copy(
            status = CommandStatus.EXECUTED,
            executedAt = LocalDateTime.now()
        )
        deviceCommandRepository.save(updated)
        return ResponseEntity.ok(mapOf("status" to "executed"))
    }

    // Dashboard sends command to device
    @PostMapping("/commands/send")
    fun sendCommand(@RequestBody request: Map<String, String>): ResponseEntity<Map<String, String>> {
        val deviceUuid = request["deviceUuid"] ?: return ResponseEntity.badRequest().body(mapOf("error" to "Missing deviceUuid"))
        val commandType = request["command"] ?: return ResponseEntity.badRequest().body(mapOf("error" to "Missing command"))

        val device = deviceRepository.findByDeviceUuid(deviceUuid)
            ?: return ResponseEntity.status(404).body(mapOf("error" to "Device not found"))

        val command = DeviceCommand(
            device = device,
            command = CommandType.valueOf(commandType)
        )
        deviceCommandRepository.save(command)
        return ResponseEntity.ok(mapOf("status" to "command queued"))
    }
}