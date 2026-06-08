package ir.maktab.shortcut.fianlproject.service;


import ir.maktab.shortcut.fianlproject.dtos.CustomerRequestDto;
import ir.maktab.shortcut.fianlproject.dtos.OfferDto;
import ir.maktab.shortcut.fianlproject.dtos.OrderRequestDto;
import ir.maktab.shortcut.fianlproject.dtos.response.HomeServiceResponseDto;
import ir.maktab.shortcut.fianlproject.dtos.response.OfferResponceDto;
import ir.maktab.shortcut.fianlproject.entity.Customer;
import ir.maktab.shortcut.fianlproject.entity.HomeService;
import ir.maktab.shortcut.fianlproject.entity.Offer;
import ir.maktab.shortcut.fianlproject.entity.Order;
import ir.maktab.shortcut.fianlproject.entity.enums.OrderStatus;
import ir.maktab.shortcut.fianlproject.exception.DuplicateException;
import ir.maktab.shortcut.fianlproject.exception.InvalidOrderException;
import ir.maktab.shortcut.fianlproject.mapper.CustomerMapper;
import ir.maktab.shortcut.fianlproject.mapper.HomeServiceMapper;
import ir.maktab.shortcut.fianlproject.mapper.OfferMapper;
import ir.maktab.shortcut.fianlproject.mapper.OrderMapper;
import ir.maktab.shortcut.fianlproject.repository.CustomerRepository;
import ir.maktab.shortcut.fianlproject.repository.HomeServiceRepository;
import ir.maktab.shortcut.fianlproject.repository.OfferRepository;
import ir.maktab.shortcut.fianlproject.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final HomeServiceRepository homeServiceRepository;
    private final HomeServiceMapper homeServiceMapper;
    private final OfferRepository offerRepository;
    private final OfferMapper offerMapper;

    public CustomerService(CustomerMapper customerMapper, CustomerRepository customerRepository, OrderRepository orderRepository, OrderMapper orderMapper, HomeServiceRepository homeServiceRepository, HomeServiceMapper homeServiceMapper, OfferRepository offerRepository, OfferMapper offerMapper) {
        this.customerMapper = customerMapper;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.homeServiceRepository = homeServiceRepository;
        this.homeServiceMapper = homeServiceMapper;
        this.offerRepository = offerRepository;
        this.offerMapper = offerMapper;
    }

    @Transactional
    public void registerCustomer(CustomerRequestDto dto) {
        if (customerRepository.existsByEmail(dto.email())) {
            throw new DuplicateException("Email already exists");
        }
        Customer customer = customerMapper.toEntity(dto);

        customerRepository.save(customer);
    }

    @Transactional
    public void updateProfile(Long id, CustomerRequestDto dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));

        customerMapper.updateEntityFromDto(dto, customer);

    }

    public List<HomeServiceResponseDto> findAllMainServices() {
        return homeServiceRepository.findByParentIsNull().stream()
                .map(service -> new HomeServiceResponseDto(
                        service.getId(),
                        service.getNameService(),
                        service.getBasePrice(),
                        service.getDescription()
                ))
                .toList();
    }

    public List<HomeServiceResponseDto> findSubServicesByParentId(Long serviceId) {

        return homeServiceRepository.findByParent_Id(serviceId).stream()
                .map(service -> new HomeServiceResponseDto(
                        service.getId(),
                        service.getNameService(),
                        service.getBasePrice(),
                        service.getDescription()))
                .toList();

    }

    public HomeServiceResponseDto findSubServiceDetails(Long serviceId) {
        HomeService homeService = homeServiceRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("HomeService not found with id: " + serviceId));
        return homeServiceMapper.toDto(homeService);
    }

    @Transactional
    public void createOrder(OrderRequestDto dto, Long customerId) {
        HomeService homeService = homeServiceRepository.findById(dto.getHomeServiceId())
                .orElseThrow(() -> new EntityNotFoundException("Service not found"));
        if (dto.getProposedPrice().compareTo(homeService.getBasePrice()) < 0) {
            throw new InvalidOrderException();
        }

        if (!homeService.getSubServices().isEmpty()) {
            throw new IllegalArgumentException("You must choose a sub service, not a parent service");
        }
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + customerId));
        Order order = orderMapper.toEntity(dto);
        order.setService(homeService);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.WAITING_FOR_OFFERS);
        orderRepository.save(order);
    }

    public List<OfferResponceDto> findAllByOrderIdOrder(Long orderId,String sort) {
        List<Offer> offers;

        if (sort == null || sort.equalsIgnoreCase("price")) {
            offers = offerRepository.findAllByOrder_IdOrderByProposedPriceAsc(orderId);
        }else if (sort.equalsIgnoreCase("rate")) {
            offers = offerRepository.findAllByOrder_IdOrderBySpecialistRateScoreDesc(orderId);

        } else {
            throw new IllegalArgumentException("Invalid sort value: " + sort);
        }
        return offerMapper.toDto(offers);
    }


    @Transactional
    public void changeOrderStatusToWaitingForSpecialist(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("order not found"));
        order.setStatus(OrderStatus.WAITING_FOR_SPECIALIST);

    }
    @Transactional
    public  void  changeOrderStatusToInProgress(Long orderId,Long offerId){

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.WAITING_FOR_SPECIALIST) {
            throw new IllegalStateException(
                    "Order must be in WAITING_FOR_SPECIALIST status");
        }

        Offer offer=offerRepository.findById(offerId)
                .orElseThrow(() -> new EntityNotFoundException("Offer not found"));

        if (LocalDateTime.now().isBefore(offer.getAppointmentTime())) {
            throw new IllegalStateException(
                    "Work cannot start before the specialist appointment time");
        }

        order.setStatus(OrderStatus.IN_PROGRESS);
    }


    @Transactional
    public void changOrderStatusToDone(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Only started orders can be marked as done");
        }

        order.setStatus(OrderStatus.DONE);
    }



}
