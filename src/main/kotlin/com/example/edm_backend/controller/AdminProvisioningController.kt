package com.example.edm_backend.controller

import com.example.edm_backend.dto.CreateEnrollmentTokenRequest
import com.example.edm_backend.dto.ProvisioningRequest
import com.example.edm_backend.entity.EnrollmentSource
import com.example.edm_backend.service.EnrollmentTokenException
import com.example.edm_backend.service.EnrollmentTokenService
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.ByteArrayOutputStream
import java.time.format.DateTimeFormatter
import java.util.UUID

@RestController
@RequestMapping("/api/admin")
class AdminProvisioningController(
    private val enrollmentTokenService: EnrollmentTokenService,
    private val objectMapper: ObjectMapper
) {

    @PostMapping("/tokens")
    fun createToken(@Valid @RequestBody request: CreateEnrollmentTokenRequest): ResponseEntity<Map<String, Any?>> {
        val issued = enrollmentTokenService.createToken(
            maxUses = request.maxUses,
            ttlHours = request.ttlHours,
            notes = request.notes
        )

        return ResponseEntity.status(201).body(
            mapOf(
                "tokenId" to issued.token.id.toString(),
                "enrollmentToken" to issued.rawToken,
                "tokenHint" to issued.token.tokenHint,
                "status" to issued.token.status.name,
                "maxUses" to issued.token.maxUses,
                "usedCount" to issued.token.usedCount,
                "expiresAt" to issued.token.expiresAt?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "createdAt" to issued.token.createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
        )
    }

    @GetMapping("/tokens")
    fun listTokens(): ResponseEntity<List<Map<String, Any?>>> {
        val response = enrollmentTokenService.listTokens().map { token ->
            mapOf(
                "tokenId" to token.id.toString(),
                "tokenHint" to token.tokenHint,
                "status" to token.status.name,
                "maxUses" to token.maxUses,
                "usedCount" to token.usedCount,
                "expiresAt" to token.expiresAt?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "createdAt" to token.createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "notes" to token.notes
            )
        }

        return ResponseEntity.ok(response)
    }

    @PostMapping("/tokens/{id}/revoke")
    fun revokeToken(@PathVariable id: UUID): ResponseEntity<Map<String, String>> {
        return try {
            val token = enrollmentTokenService.revoke(id)
            ResponseEntity.ok(
                mapOf(
                    "status" to token.status.name,
                    "tokenId" to token.id.toString(),
                    "message" to "Token revoked"
                )
            )
        } catch (ex: EnrollmentTokenException) {
            ResponseEntity.badRequest().body(
                mapOf(
                    "status" to ex.code.name,
                    "message" to ex.message
                )
            )
        }
    }

    @PostMapping("/provisioning/payload")
    fun createProvisioningPayload(
        @Valid @RequestBody request: ProvisioningRequest,
        servletRequest: HttpServletRequest
    ): ResponseEntity<Map<String, Any?>> {
        val issued = enrollmentTokenService.createToken(
            maxUses = request.maxUses,
            ttlHours = request.ttlHours,
            notes = request.notes
        )

        val enrollmentUrl = request.enrollmentUrl ?: defaultEnrollUrl(servletRequest)
        return ResponseEntity.status(201).body(
            mapOf(
                "enrollmentUrl" to enrollmentUrl,
                "enrollmentToken" to issued.rawToken,
                "enrollmentSource" to EnrollmentSource.QR_SETUP.name,
                "tokenId" to issued.token.id.toString(),
                "tokenHint" to issued.token.tokenHint
            )
        )
    }

    @PostMapping("/provisioning/config-payload")
    fun createConfigProvisioningPayload(
        @Valid @RequestBody request: ProvisioningRequest,
        servletRequest: HttpServletRequest
    ): ResponseEntity<Map<String, Any?>> {
        val issued = enrollmentTokenService.createToken(
            maxUses = request.maxUses,
            ttlHours = request.ttlHours,
            notes = request.notes
        )

        val enrollmentUrl = request.enrollmentUrl ?: defaultEnrollUrl(servletRequest)
        return ResponseEntity.status(201).body(
            mapOf(
                "enrollmentUrl" to enrollmentUrl,
                "enrollmentToken" to issued.rawToken,
                "enrollmentSource" to EnrollmentSource.CONFIG_PUSH.name,
                "tokenId" to issued.token.id.toString(),
                "tokenHint" to issued.token.tokenHint
            )
        )
    }

    @PostMapping("/provisioning/qr")
    fun createProvisioningQr(
        @Valid @RequestBody request: ProvisioningRequest,
        servletRequest: HttpServletRequest
    ): ResponseEntity<ByteArray> {
        val issued = enrollmentTokenService.createToken(
            maxUses = request.maxUses,
            ttlHours = request.ttlHours,
            notes = request.notes
        )

        val enrollmentUrl = request.enrollmentUrl ?: defaultEnrollUrl(servletRequest)
        val payload = mapOf(
            "enrollmentUrl" to enrollmentUrl,
            "enrollmentToken" to issued.rawToken,
            "enrollmentSource" to EnrollmentSource.QR_SETUP.name,
            "tokenId" to issued.token.id.toString()
        )

        val qrText = objectMapper.writeValueAsString(payload)
        val matrix = MultiFormatWriter().encode(qrText, BarcodeFormat.QR_CODE, 420, 420)
        val outputStream = ByteArrayOutputStream()
        MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream)

        val headers = HttpHeaders()
        headers.contentType = MediaType.IMAGE_PNG
        headers.add(HttpHeaders.CACHE_CONTROL, "no-store")

        return ResponseEntity.ok()
            .headers(headers)
            .body(outputStream.toByteArray())
    }

    private fun defaultEnrollUrl(request: HttpServletRequest): String {
        val scheme = request.scheme
        val serverName = request.serverName
        val serverPort = request.serverPort
        val path = request.contextPath + "/api/enroll"

        val isDefaultPort = (scheme == "http" && serverPort == 80) || (scheme == "https" && serverPort == 443)
        return if (isDefaultPort) {
            "$scheme://$serverName$path"
        } else {
            "$scheme://$serverName:$serverPort$path"
        }
    }
}
