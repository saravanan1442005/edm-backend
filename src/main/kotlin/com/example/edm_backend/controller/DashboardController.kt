package com.example.edm_backend.controller

import com.example.edm_backend.entity.DeviceStatus
import com.example.edm_backend.repository.AppInventoryRepository
import com.example.edm_backend.repository.DeviceInfoRepository
import com.example.edm_backend.repository.DeviceRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.UUID

@Controller
@RequestMapping("/dashboard")
class DashboardController(
    private val deviceRepository: DeviceRepository,
    private val deviceInfoRepository: DeviceInfoRepository,
    private val appInventoryRepository: AppInventoryRepository
) {

    @GetMapping
    fun dashboard(model: Model): String {
        val devices = deviceRepository.findAll()
        val inactiveDevices = devices.count { it.status == DeviceStatus.INACTIVE }
        model.addAttribute("devices", devices)
        model.addAttribute("totalDevices", devices.size)
        model.addAttribute("inactiveDevices", inactiveDevices)
        return "dashboard"
    }

    @GetMapping("/device/{id}")
    fun deviceDetail(@PathVariable id: UUID, model: Model): String {
        val device = deviceRepository.findById(id).orElse(null)
            ?: return "redirect:/dashboard"

        val deviceInfoList = deviceInfoRepository.findByDevice(device)
        val appInventory = appInventoryRepository.findByDevice(device)

        model.addAttribute("device", device)
        model.addAttribute("deviceInfo", deviceInfoList.lastOrNull())
        model.addAttribute("apps", appInventory)
        model.addAttribute("totalApps", appInventory.size)
        return "dashboard-detail"
    }

    @GetMapping("/provisioning")
    fun provisioning(model: Model): String {
        return "provisioning"
    }
}