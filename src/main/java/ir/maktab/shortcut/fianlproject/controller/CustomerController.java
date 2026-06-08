package ir.maktab.shortcut.fianlproject.controller;

import ir.maktab.shortcut.fianlproject.dtos.CustomerRequestDto;
import ir.maktab.shortcut.fianlproject.dtos.OrderRequestDto;
import ir.maktab.shortcut.fianlproject.dtos.response.HomeServiceResponseDto;
import ir.maktab.shortcut.fianlproject.dtos.response.OfferResponceDto;
import ir.maktab.shortcut.fianlproject.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

   //t
    @PostMapping
    public ResponseEntity<String> registerCustomer(@RequestBody  @Valid CustomerRequestDto dto) {
        customerService.registerCustomer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("مشتری با موفقیت ثبت ‌نام شد.");
    }
    //t
    @PutMapping("/{customerId}")
    public ResponseEntity<Void> updateCustomer(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerRequestDto dto
    ) {
        customerService.updateProfile(customerId, dto);
        return ResponseEntity.noContent().build();
    }
    //t
   @GetMapping("/services")
    public ResponseEntity<List<HomeServiceResponseDto>> getAllMainServices(){
       List<HomeServiceResponseDto> services= customerService.findAllMainServices();
       return ResponseEntity.ok(services);
   }
   //t
    @GetMapping("/services/{serviceId}/subservices")
    public  ResponseEntity<List<HomeServiceResponseDto>> getSubServices(
            @PathVariable Long serviceId){
       List<HomeServiceResponseDto> services= customerService.findSubServicesByParentId(serviceId);
       return  ResponseEntity.ok(services);
   }
    //t
    @GetMapping("/services/{serviceId}")
    public ResponseEntity<HomeServiceResponseDto> getSubServiceDetails(
            @PathVariable Long serviceId) {

        HomeServiceResponseDto response = customerService.findSubServiceDetails(serviceId);
        return ResponseEntity.ok(response);
    }
    //t
    @PostMapping("/{customerId}/orders")
    public ResponseEntity<Void> createOrder(
            @RequestBody @Valid OrderRequestDto dto,
            @PathVariable Long customerId) {

        customerService.createOrder(dto, customerId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }



    @GetMapping("/orders/{orderId}/offers")
    public ResponseEntity<List<OfferResponceDto>> getOffers(
            @PathVariable Long orderId,
            @RequestParam String sortBy
    ) {
        return ResponseEntity.ok(
                customerService.findAllByOrderIdOrder(orderId, sortBy)
        );
    }

    @PostMapping("/{orderId}/select-offer")
    public ResponseEntity<Void> changeToWaitingForSpecialist(
            @PathVariable Long orderId) {

        customerService.changeOrderStatusToWaitingForSpecialist(orderId);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{orderId}/start")
    public ResponseEntity<Void> startWork(
            @PathVariable Long orderId,
            @RequestParam Long offerId) {

        customerService.changeOrderStatusToInProgress(orderId, offerId);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{orderId}/complete")
    public ResponseEntity<Void> completeOrder(
            @PathVariable Long orderId) {

     customerService.changOrderStatusToDone(orderId);
        return ResponseEntity.ok().build();
    }

}
