package ir.maktab.shortcut.fianlproject.service;

import ir.maktab.shortcut.fianlproject.dtos.CustomerRequestDto;
import ir.maktab.shortcut.fianlproject.dtos.OrderRequestDto;
import ir.maktab.shortcut.fianlproject.dtos.response.HomeServiceDetailsResponseDto;
import ir.maktab.shortcut.fianlproject.dtos.response.HomeServiceResponseDto;
import ir.maktab.shortcut.fianlproject.entity.Customer;
import ir.maktab.shortcut.fianlproject.entity.HomeService;
import ir.maktab.shortcut.fianlproject.entity.Order;
import ir.maktab.shortcut.fianlproject.mapper.CustomerMapper;
import ir.maktab.shortcut.fianlproject.mapper.HomeServiceMapper;
import ir.maktab.shortcut.fianlproject.mapper.OrderMapper;
import ir.maktab.shortcut.fianlproject.repository.CustomerRepository;
import ir.maktab.shortcut.fianlproject.repository.HomeServiceRepository;
import ir.maktab.shortcut.fianlproject.repository.OrderRepository;
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
        Customer customer = customerMapper.toEntity(dto);
        customerRepository.save(customer);
    }

    public void updateProfile(Long id,CustomerRequestDto dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("مشتری یافت نشد"));
        customerMapper.updateEntityFromDto(dto,customer);
        customerRepository.save(customer);

    }
    public  void getCustomerProfile(){}

    public void createOrder(OrderRequestDto dto){
        Order order=orderMapper.toEntity(dto);
        orderRepository.save(order);
    }

    public List<HomeServiceResponseDto> findAllMainServices(){
       return homeServiceRepository.findByParentIsNull()  .stream()
               .map(service -> new HomeServiceResponseDto(service.getServiceId(), service.getNameService()))
               .toList();

    }
    public List<HomeServiceResponseDto> findSubServicesByMainService(Long parentId) {
        return homeServiceRepository.findByParent_ServiceId(parentId) .stream()
                .map(service -> new HomeServiceResponseDto(service.getServiceId(), service.getNameService()))
                .toList();

    }
        public HomeServiceDetailsResponseDto findSubServiceDetails(Long serviceId){
        HomeService homeService= homeServiceRepository.findHomeServiceByServiceId(serviceId);
       return homeServiceMapper.toDto(homeService);
    }



}
