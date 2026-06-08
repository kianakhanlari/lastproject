package ir.maktab.shortcut.fianlproject.controller;

import ir.maktab.shortcut.fianlproject.dtos.HomeServiceRequestDto;
import ir.maktab.shortcut.fianlproject.dtos.SpecialistRequestDto;
import ir.maktab.shortcut.fianlproject.dtos.response.HomeServiceResponseDto;
import ir.maktab.shortcut.fianlproject.dtos.response.SpecialistResponseDto;
import ir.maktab.shortcut.fianlproject.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private  final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

   //t
    @PostMapping("/services")

    public ResponseEntity<String> createService(@Valid @RequestBody HomeServiceRequestDto dto) {
       adminService.createService(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Service created successfully");
    }
    //t
    @PutMapping("/services/{serviceId}")
    public ResponseEntity<Void> updateService(
            @PathVariable Long serviceId,
            @Valid @RequestBody HomeServiceRequestDto dto
    ) {
        adminService.updateService(serviceId, dto);
        return ResponseEntity.noContent().build();
    }
   //t
    @DeleteMapping("/services/{serviceId}")
    public ResponseEntity<Void> deleteService(@PathVariable Long serviceId) {
        adminService.deleteService(serviceId);
        return ResponseEntity.noContent().build();
    }
    //t
    @DeleteMapping("/specialists/{specialistId}")
    public ResponseEntity<Void> deleteSpecialist(
            @PathVariable Long specialistId
    ) {
        adminService.deleteSpecialist(specialistId);
        return ResponseEntity.noContent().build();
    }
   //t
    @GetMapping("/services/{serviceId}/sub-services")
    public ResponseEntity<List<HomeServiceResponseDto>> getSubServices(
            @PathVariable Long serviceId) {
        List<HomeServiceResponseDto> subServices = adminService.getSubServices(serviceId);
        return ResponseEntity.ok(subServices);
    }
    //t
    @PostMapping("/specialists")
    public ResponseEntity<Void> addSpecialist(
            @Valid @RequestBody SpecialistRequestDto dto) {

        adminService.addSpecialist(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    //t
    @GetMapping("/specialists")
    public ResponseEntity<Page<SpecialistResponseDto>> getWaitingAndNewSpecialists(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<SpecialistResponseDto> result = adminService.getListWaitingAndNewSpecialists(pageable);
        return ResponseEntity.ok(result);
    }
    //t
    @PutMapping ("/specialists/{specialistId}/approve")
    public ResponseEntity<Void> approveSpecialist(
            @PathVariable Long specialistId,
            @RequestParam List<Long> serviceIds
    ) {
        adminService.approveSpecialist(specialistId, serviceIds);
        return ResponseEntity.noContent().build();
    }

}
