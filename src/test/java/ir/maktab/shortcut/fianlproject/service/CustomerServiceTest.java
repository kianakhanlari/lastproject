package ir.maktab.shortcut.fianlproject.service;

import ir.maktab.shortcut.fianlproject.dtos.CustomerRequestDto;
import ir.maktab.shortcut.fianlproject.dtos.OrderRequestDto;
import ir.maktab.shortcut.fianlproject.dtos.response.HomeServiceResponseDto;
import ir.maktab.shortcut.fianlproject.entity.Customer;
import ir.maktab.shortcut.fianlproject.entity.HomeService;
import ir.maktab.shortcut.fianlproject.exception.DuplicateException;
import ir.maktab.shortcut.fianlproject.exception.InvalidOrderException;
import ir.maktab.shortcut.fianlproject.mapper.CustomerMapper;
import ir.maktab.shortcut.fianlproject.mapper.HomeServiceMapper;
import ir.maktab.shortcut.fianlproject.repository.CustomerRepository;
import ir.maktab.shortcut.fianlproject.repository.HomeServiceRepository;
import ir.maktab.shortcut.fianlproject.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerMapper customerMapper;
    @Mock
    private HomeServiceMapper homeServiceMapper;
    @Mock
    private HomeServiceRepository homeServiceRepository;
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private CustomerService customerService;


    @Test
    void shouldThrowDuplicateExceptionWhenEmailAlreadyExists() {
        CustomerRequestDto dto = new CustomerRequestDto(
                "Ali Ahmadi",
                "Ali12345",
                "ali@gmail.com"
        );

        when(customerRepository.existsByEmail(dto.email())).thenReturn(true);

        assertThrows(DuplicateException.class, () -> customerService.registerCustomer(dto));

        verify(customerRepository).existsByEmail(dto.email());
        verify(customerMapper, never()).toEntity(any());
        verify(customerRepository, never()).save(any());
    }
    @Test
    void shouldRegisterCustomerSuccessfully() {

        CustomerRequestDto dto = new CustomerRequestDto(
                "Ali Ahmadi",

                "Ali12345",
                "ali@gmail.com"
        );

        Customer customer = new Customer();

        when(customerRepository.existsByEmail("ali@gmail.com")).thenReturn(false);
        when(customerMapper.toEntity(dto)).thenReturn(customer);

        customerService.registerCustomer(dto);

        verify(customerRepository).existsByEmail("ali@gmail.com");
        verify(customerMapper).toEntity(dto);
        verify(customerRepository).save(customer);
    }


    @Test
    void shouldThrowDuplicateExceptionWhenThereIsAlreadyEmail() {

        CustomerRequestDto dto = new CustomerRequestDto(
                "Ali Ahmadi",

                "Ali12345",
                "ali@gmail.com"
        );

        when(customerRepository.existsByEmail("ali@gmail.com")).thenReturn(true);

        DuplicateException exception =
                assertThrows(DuplicateException.class,
                        () -> customerService.registerCustomer(dto));

        assertEquals("Email already exists", exception.getMessage());

        verify(customerRepository).existsByEmail("ali@gmail.com");
        verify(customerRepository, never()).save(any());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenCustomerDoesNotExist() {
        Long id = 1L;
        CustomerRequestDto dto = new CustomerRequestDto(
                "Ali Ahmadi",

                "Ali12345",
                "ali@gmail.com"
        );

        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> customerService.updateProfile(id, dto)
        );

        assertEquals("Customer not found with id: " + id, exception.getMessage());

        verify(customerRepository).findById(id);
        verify(customerMapper, never()).updateEntityFromDto(any(), any());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void shouldUpdateCustomerProfileWhenCustomerExists() {
        Long id = 1L;
        CustomerRequestDto dto = new CustomerRequestDto(
                "Ali Ahmadi",
                "Ali12345",
                "ali@gmail.com"
        );

        Customer customer = new Customer();
        customer.setId(id);

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        customerService.updateProfile(id, dto);

        verify(customerRepository).findById(id);
        verify(customerMapper).updateEntityFromDto(dto, customer);
        verify(customerRepository).save(customer);
    }

    @Test
    void shouldReturnAllMainServices() {

        HomeService service1 = new HomeService();
        service1.setId(1L);
        service1.setNameService("Cleaning");
        service1.setDescription("Home cleaning service");

        HomeService service2 = new HomeService();
        service2.setId(2L);
        service2.setNameService("Plumbing");
        service2.setDescription("Pipe repair service");

        when(homeServiceRepository.findByParentIsNull())
                .thenReturn(List.of(service1, service2));

        List<HomeServiceResponseDto> result = customerService.findAllMainServices();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).getHomeServiceId());
        assertEquals("Cleaning", result.get(0).getNameService());
        assertEquals("Home cleaning service", result.get(0).getDescription());

        assertEquals(2L, result.get(1).getHomeServiceId());
        assertEquals("Plumbing", result.get(1).getNameService());
        assertEquals("Pipe repair service", result.get(1).getDescription());

        verify(homeServiceRepository).findByParentIsNull();
    }


    @Test
    void shouldReturnEmptyListWhenNoMainServicesExist() {

        when(homeServiceRepository.findByParentIsNull()).thenReturn(Collections.emptyList());


        List<HomeServiceResponseDto> result = customerService.findAllMainServices();


        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(homeServiceRepository).findByParentIsNull();
    }


    @Test
    void findSubServicesByParentId() {
        HomeService service1 = new HomeService();
        service1.setId(1L);
        service1.setNameService("نظافت منزل");
        //   service1.setBasePrice(200000.0);
        service1.setDescription("خدمات نظافت کامل منزل");

        HomeService service2 = new HomeService();
        service2.setId(2L);
        service2.setNameService("لوله‌کشی");
        //  service2.setBasePrice(350000.0);
        service2.setDescription("خدمات تعمیر و نصب لوله");

        List<HomeService> homeServices = List.of(service1, service2);

    }

    @Test
    void findSubServicesByParentId_whenExists_shouldReturnMappedDtos() {

        Long parentId = 10L;

        HomeService sub1 = new HomeService();
        sub1.setId(1L);
        sub1.setNameService("نظافت آشپزخانه");
        // sub1.setBasePrice(150000.0);
        sub1.setDescription("نظافت کامل آشپزخانه");

        HomeService sub2 = new HomeService();
        sub2.setId(2L);
        sub2.setNameService("نظافت سرویس بهداشتی");
        //sub2.setBasePrice(180000.0);
        sub2.setDescription("نظافت و ضدعفونی سرویس");

        when(homeServiceRepository.findByParent_Id(parentId))
                .thenReturn(List.of(sub1, sub2));


        List<HomeServiceResponseDto> result =
                customerService.findSubServicesByParentId(parentId);


        assertNotNull(result);
        assertEquals(2, result.size());

        HomeServiceResponseDto dto1 = result.get(0);
        assertEquals(1L, dto1.getHomeServiceId());
        assertEquals("نظافت آشپزخانه", dto1.getNameService());
        // assertEquals(150000.0, dto1.basePrice());
        assertEquals("نظافت کامل آشپزخانه", dto1.getDescription());

        HomeServiceResponseDto dto2 = result.get(1);
        assertEquals(2L, dto2.getHomeServiceId());
        assertEquals("نظافت سرویس بهداشتی", dto2.getNameService());
        //assertEquals(180000.0, dto2.basePrice());
        assertEquals("نظافت و ضدعفونی سرویس", dto2.getDescription());

        verify(homeServiceRepository).findByParent_Id(parentId);
        verifyNoMoreInteractions(homeServiceRepository);
    }

    @Test
    void findSubServicesByParentId_whenEmpty_shouldReturnEmptyList() {

        Long parentId = 10L;

        when(homeServiceRepository.findByParent_Id(parentId))
                .thenReturn(Collections.emptyList());


        List<HomeServiceResponseDto> result =
                customerService.findSubServicesByParentId(parentId);


        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(homeServiceRepository).findByParent_Id(parentId);
        verifyNoMoreInteractions(homeServiceRepository);
    }

    @Test
    void shouldThrowEntityNotFoundwhenServiceNotFound() {
        Long customerId = 1L;
        OrderRequestDto dto = mock(OrderRequestDto.class);
        when(dto.getHomeServiceId()).thenReturn(2L);
        when(homeServiceRepository.findById(2L))
                .thenReturn(Optional.empty());
        EntityNotFoundException e =
                assertThrows(EntityNotFoundException.class, () -> customerService.createOrder(dto, customerId));
        assertEquals("Service not found", e.getMessage());

        verify(orderRepository,never()).save(any());

    }
    @Test
    void shouldThrowEntityNotFoundWhenServiceDoesNotExist() {

        Long serviceId = 1L;

        when(homeServiceRepository.findById(serviceId))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class,
                        () -> customerService.findSubServiceDetails(serviceId));

        assertEquals("HomeService not found with id: 1", exception.getMessage());

        verify(homeServiceRepository).findById(serviceId);
        verify(homeServiceMapper, never()).toDto(any());
    }
    @Test
    void shouldReturnSubServiceDetailsSuccessfully() {

        Long serviceId = 1L;

        HomeService service = new HomeService();
        service.setId(serviceId);
        service.setNameService("Cleaning");

        HomeServiceResponseDto dto =
                new HomeServiceResponseDto(serviceId, "Cleaning", null, null);

        when(homeServiceRepository.findById(serviceId))
                .thenReturn(Optional.of(service));

        when(homeServiceMapper.toDto(service))
                .thenReturn(dto);

        HomeServiceResponseDto result = customerService.findSubServiceDetails(serviceId);

        assertNotNull(result);
        assertEquals(serviceId, result.getHomeServiceId());
        assertEquals("Cleaning", result.getNameService());

        verify(homeServiceRepository).findById(serviceId);
        verify(homeServiceMapper).toDto(service);
    }


    @Test
    void createOrder_whenProposedPriceLowerThanBasePrice_shouldThrowInvalidOrderException() {

        Long customerId = 1L;

        OrderRequestDto dto = mock(OrderRequestDto.class);
        when(dto.getHomeServiceId()).thenReturn(10L);
        when(dto.getProposedPrice()).thenReturn(new BigDecimal("90"));

        HomeService homeService = mock(HomeService.class);
        when(homeService.getBasePrice()).thenReturn(new BigDecimal("100"));


        when(homeServiceRepository.findById(10L)).thenReturn(Optional.of(homeService));


        assertThrows(InvalidOrderException.class,
                () -> customerService.createOrder(dto, customerId));

        verify(homeServiceRepository).findById(10L);
        verify(homeService).getBasePrice();


        verifyNoInteractions(customerRepository);
    }
    @Test
    void shouldThrowIllegalArgumentExceptionWhenParentServiceSelected() {

        Long customerId = 1L;

        OrderRequestDto dto = mock(OrderRequestDto.class);
        when(dto.getHomeServiceId()).thenReturn(1L);
        when(dto.getProposedPrice()).thenReturn(new BigDecimal("300"));

        HomeService service = new HomeService();
        service.setBasePrice(new BigDecimal("200"));
        service.setSubServices(Set.of(new HomeService()));

        when(homeServiceRepository.findById(1L))
                .thenReturn(Optional.of(service));

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> customerService.createOrder(dto, customerId));

        assertEquals("You must choose a sub service, not a parent service",
                exception.getMessage());

        verify(orderRepository, never()).save(any());
    }
    @Test
    void shouldThrowInvalidOrderExceptionWhenPriceLessThanBasePrice() {

        Long customerId = 1L;

        OrderRequestDto dto = mock(OrderRequestDto.class);
        when(dto.getHomeServiceId()).thenReturn(1L);
        when(dto.getProposedPrice()).thenReturn(new BigDecimal("100"));

        HomeService service = new HomeService();
        service.setBasePrice(new BigDecimal("200"));

        when(homeServiceRepository.findById(1L))
                .thenReturn(Optional.of(service));

        assertThrows(InvalidOrderException.class,
                () -> customerService.createOrder(dto, customerId));

        verify(homeServiceRepository).findById(1L);
        verify(orderRepository, never()).save(any());
    }
    @Test
    void shouldThrowEntityNotFoundWhenServiceNotFound() {

        Long customerId = 1L;
        OrderRequestDto dto = mock(OrderRequestDto.class);

        when(dto.getHomeServiceId()).thenReturn(2L);
        when(homeServiceRepository.findById(2L)).thenReturn(Optional.empty());

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class,
                        () -> customerService.createOrder(dto, customerId));

        assertEquals("Service not found", exception.getMessage());

        verify(homeServiceRepository).findById(2L);
        verify(orderRepository, never()).save(any());
    }



}