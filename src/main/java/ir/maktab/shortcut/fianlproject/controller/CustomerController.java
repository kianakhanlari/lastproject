package ir.maktab.shortcut.fianlproject.controller;

import ir.maktab.shortcut.fianlproject.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @PostMapping("/register/customer")
    public ResponseEntity<String> registerCustomer(@RequestBody CustomerRequestDto dto) {
        customerService.registerCustomer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("مشتری با موفقیت ثبت ‌نام شد.");

    }

}
