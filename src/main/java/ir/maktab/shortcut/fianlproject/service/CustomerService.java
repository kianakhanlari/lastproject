package ir.maktab.shortcut.fianlproject.service;


import ir.maktab.shortcut.fianlproject.dtos.CustomerRequestDto;
import ir.maktab.shortcut.fianlproject.dtos.OrderRequestDto;
import ir.maktab.shortcut.fianlproject.dtos.response.HomeServiceResponseDto;
import ir.maktab.shortcut.fianlproject.entity.Customer;
import ir.maktab.shortcut.fianlproject.entity.HomeService;
import ir.maktab.shortcut.fianlproject.entity.Order;
import ir.maktab.shortcut.fianlproject.entity.enums.OrderStatus;
import ir.maktab.shortcut.fianlproject.exception.DuplicateException;
import ir.maktab.shortcut.fianlproject.exception.InvalidOrderException;
import ir.maktab.shortcut.fianlproject.mapper.CustomerMapper;
import ir.maktab.shortcut.fianlproject.mapper.HomeServiceMapper;
import ir.maktab.shortcut.fianlproject.mapper.OrderMapper;
import ir.maktab.shortcut.fianlproject.repository.CustomerRepository;
import ir.maktab.shortcut.fianlproject.repository.HomeServiceRepository;
import ir.maktab.shortcut.fianlproject.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private  final OrderMapper orderMapper;
    private final HomeServiceRepository homeServiceRepository;
    private final HomeServiceMapper homeServiceMapper;

    public CustomerService(CustomerMapper customerMapper, CustomerRepository customerRepository, OrderRepository orderRepository, OrderMapper orderMapper, HomeServiceRepository homeServiceRepository, HomeServiceMapper homeServiceMapper) {
        this.customerMapper = customerMapper;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.homeServiceRepository = homeServiceRepository;
        this.homeServiceMapper = homeServiceMapper;
    }


    public void registerCustomer(CustomerRequestDto dto) {
        if (customerRepository.existsByEmail(dto.email())) {
            throw new DuplicateException();
        }
        Customer customer = customerMapper.toEntity(dto);
        customerRepository.save(customer);
    }

    public void updateProfile(Long id, CustomerRequestDto dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));

        customerMapper.updateEntityFromDto(dto,customer);

        customerRepository.save(customer);

    }
    public List<HomeServiceResponseDto> findAllMainServices() {
        return homeServiceRepository.findByParentIsNull().stream()
                .map(service -> new HomeServiceResponseDto(
                        service.getServiceId(),
                        service.getNameService(),
                        service.getBasePrice(),
                        service.getDescription()
                ))
                .toList();
    }

    public List<HomeServiceResponseDto> findSubServicesByParentId(Long serviceId) {

        return homeServiceRepository.findByParent_ServiceId(serviceId) .stream()
                .map(service -> new HomeServiceResponseDto(
                        service.getServiceId(),
                        service.getNameService(),
                        service.getBasePrice(),
                        service.getDescription()))
                .toList();

    }

    public HomeServiceResponseDto findSubServiceDetails(Long serviceId){
        HomeService homeService = homeServiceRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("HomeService not found with id: " + serviceId));
       return homeServiceMapper.toDto(homeService);
    }

    public void createOrder(OrderRequestDto dto,Long customerId){
        HomeService homeService = homeServiceRepository.findById(dto.getHomeServiceId())
                .orElseThrow(() -> new EntityNotFoundException("Service not found"));
        if (dto.getProposedPrice().compareTo(homeService.getBasePrice()) < 0) {
            throw new InvalidOrderException();
        }

        if (homeService.getSubServices() != null && !homeService.getSubServices().isEmpty()) {
            throw new IllegalArgumentException("You must choose a sub service, not a parent service");
        }
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + customerId));
        Order order=orderMapper.toEntity(dto);
        order.setService(homeService);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.WAITING_FOR_SPECIALIST);
        orderRepository.save(order);
    }



}
