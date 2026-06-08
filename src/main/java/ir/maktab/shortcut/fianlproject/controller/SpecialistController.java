package ir.maktab.shortcut.fianlproject.controller;


import ir.maktab.shortcut.fianlproject.dtos.OfferDto;
import ir.maktab.shortcut.fianlproject.dtos.SpecialistRequestDto;
import ir.maktab.shortcut.fianlproject.service.SpecialistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/specialists")
public class SpecialistController {
    private final SpecialistService specialistService;


    public SpecialistController(SpecialistService specialistService) {
        this.specialistService = specialistService;
    }


  /*  @PostMapping( consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
    }*/

    //بپرسم
/*  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> register(
          @RequestPart("data") SpecialistRequestDto dto,
          @RequestPart("file") MultipartFile file) throws IOException {

      specialistService.registerSpecialist(dto, file.getBytes());

      return ResponseEntity.ok().build();
  }*/
    //t
    @PutMapping("/{specialistId}")
    public ResponseEntity<Void> updateSpecialist(
            @PathVariable Long specialistId,
            @Validated @RequestBody SpecialistRequestDto dto
    ) {
        specialistService.updateSpecialistProfile(specialistId, dto);
        return ResponseEntity.noContent().build();
    }

    //t
    @PostMapping("/{specialistId}/orders/{orderId}/offers")
    public ResponseEntity<Void> createOffer(
            @PathVariable Long orderId,
            @PathVariable Long specialistId,
            @Validated @RequestBody OfferDto dto
    ) {
        specialistService.createOffer(orderId, specialistId, dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
