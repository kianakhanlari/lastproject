package ir.maktab.shortcut.fianlproject.controller;

import ir.maktab.shortcut.fianlproject.dtos.SpecialistRequestDto;
import ir.maktab.shortcut.fianlproject.service.FileStorageService;
import ir.maktab.shortcut.fianlproject.service.SpecialistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
@RestController
public class SpecialistController {
    private final SpecialistService specialistService;
    private  final FileStorageService fileStorageService;

    public SpecialistController(SpecialistService specialistService, FileStorageService fileStorageService) {
        this.specialistService = specialistService;
        this.fileStorageService = fileStorageService;
    }


    @PostMapping(value = "/register/specialist", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> registerSpecialist(
            @RequestPart("data") SpecialistRequestDto dto,
            @RequestPart("file") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is required");
        }

        try {

            String filePath = fileStorageService.storeFile(file);


            specialistService.registerSpecialist(dto, filePath);

            return ResponseEntity.ok("Specialist registered successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

}
