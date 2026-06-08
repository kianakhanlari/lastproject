package ir.maktab.shortcut.fianlproject.service;

import ir.maktab.shortcut.fianlproject.dtos.HomeServiceRequestDto;
import ir.maktab.shortcut.fianlproject.dtos.SpecialistRequestDto;
import ir.maktab.shortcut.fianlproject.dtos.response.HomeServiceResponseDto;
import ir.maktab.shortcut.fianlproject.dtos.response.SpecialistResponseDto;
import ir.maktab.shortcut.fianlproject.entity.HomeService;
import ir.maktab.shortcut.fianlproject.entity.Specialist;
import ir.maktab.shortcut.fianlproject.entity.enums.SpecialistStatus;
import ir.maktab.shortcut.fianlproject.exception.DuplicateException;
import ir.maktab.shortcut.fianlproject.exception.HomeServiceAlreadyExistsException;
import ir.maktab.shortcut.fianlproject.mapper.HomeServiceMapper;
import ir.maktab.shortcut.fianlproject.mapper.SpecialistMapper;
import ir.maktab.shortcut.fianlproject.repository.HomeServiceRepository;
import ir.maktab.shortcut.fianlproject.repository.SpecialistRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.HeuristicCommitException;
import org.checkerframework.checker.units.qual.N;
import org.h2.command.dml.MergeUsing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.security.DigestException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
    @Mock
    private HomeServiceRepository homeServiceRepository;
    @Mock
    private HomeServiceMapper homeServiceMapper;
    @Mock
    private SpecialistRepository specialistRepository;
    @Mock
    private SpecialistMapper specialistMapper;
    @InjectMocks
    private AdminService adminService;


    @Test
    void shouldThrowEntityNotFoundExceptionWhenHomeServiceParentnotExist() {
        HomeServiceRequestDto dto = mock(HomeServiceRequestDto.class);
        HomeService homeService = new HomeService();
        when(dto.parentId()).thenReturn(1L);
        when(homeServiceRepository.findById(dto.parentId()))
                .thenReturn(Optional.empty());
        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> adminService.createService(dto));
        assertEquals("Parent service not found with id: 1", exception.getMessage());
        verify(homeServiceRepository, never()).save(any());
    }

    @Test
    void shouldThrowHomeServiceAlreadyExistsExceptionWhenThereIsSameHomeService() {

        HomeServiceRequestDto dto = mock(HomeServiceRequestDto.class);
        when(dto.parentId()).thenReturn(null);
        when(dto.nameService()).thenReturn("barghkar");

        when(homeServiceRepository.findByNameServiceIgnoreCase("barghkar"))
                .thenReturn(Optional.of(new HomeService()));

        HomeServiceAlreadyExistsException exception =
                assertThrows(
                        HomeServiceAlreadyExistsException.class,
                        () -> adminService.createService(dto)
                );

        assertEquals(
                "HomeService with name 'barghkar' already exists",
                exception.getMessage()
        );

        verify(homeServiceRepository, never()).save(any());
    }

    @Test
    void shouldCreateHomeServiceSuccessfully() {

        HomeServiceRequestDto dto = mock(HomeServiceRequestDto.class);
        HomeService homeService = new HomeService();

        when(dto.parentId()).thenReturn(null);
        when(dto.nameService()).thenReturn("barghkar");

        when(homeServiceRepository.findByNameServiceIgnoreCase("barghkar"))
                .thenReturn(Optional.empty());

        when(homeServiceMapper.toEntity(dto))
                .thenReturn(homeService);

        adminService.createService(dto);

        verify(homeServiceMapper).toEntity(dto);
        verify(homeServiceRepository).save(homeService);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenHomeServiceNotExists() {

        Long serviceId = 1L;
        HomeServiceRequestDto dto = mock(HomeServiceRequestDto.class);

        when(homeServiceRepository.findById(serviceId))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception =
                assertThrows(
                        EntityNotFoundException.class,
                        () -> adminService.updateService(serviceId, dto)
                );

        assertEquals(
                "there isnt HomeService With this id",
                exception.getMessage()
        );

        verify(homeServiceRepository).findById(serviceId);
        verify(homeServiceRepository, never()).save(any());
        verifyNoInteractions(homeServiceMapper);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenServiceIdAndParentIdAreSame() {

        Long serviceId = 1L;

        HomeServiceRequestDto dto = mock(HomeServiceRequestDto.class);
        HomeService homeService = new HomeService();

        when(homeServiceRepository.findById(serviceId))
                .thenReturn(Optional.of(homeService));

        when(dto.parentId()).thenReturn(1L);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> adminService.updateService(serviceId, dto)
                );

        assertEquals(
                "Service cannot be parent of itself",
                exception.getMessage()
        );

        verify(homeServiceRepository).findById(serviceId);
        verify(homeServiceRepository, never()).save(any());
        verifyNoInteractions(homeServiceMapper);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenParentDoesNotExist() {

        Long serviceId = 1L;

        HomeServiceRequestDto dto = mock(HomeServiceRequestDto.class);
        HomeService homeService = new HomeService();

        when(homeServiceRepository.findById(serviceId))
                .thenReturn(Optional.of(homeService));

        when(dto.parentId()).thenReturn(2L);

        when(homeServiceRepository.findById(2L))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception =
                assertThrows(
                        EntityNotFoundException.class,
                        () -> adminService.updateService(serviceId, dto)
                );

        assertEquals(
                "Parent service not found: 2",
                exception.getMessage()
        );

        verify(homeServiceRepository).findById(serviceId);
        verify(homeServiceRepository).findById(2L);

        verify(homeServiceRepository, never()).save(any());
        verifyNoInteractions(homeServiceMapper);
    }

    @Test
    void shouldUpdateServiceSuccessfully() {

        Long serviceId = 1L;

        HomeServiceRequestDto dto = mock(HomeServiceRequestDto.class);

        HomeService homeService = new HomeService();
        HomeService parent = new HomeService();

        when(homeServiceRepository.findById(serviceId))
                .thenReturn(Optional.of(homeService));

        when(dto.parentId()).thenReturn(2L);

        when(homeServiceRepository.findById(2L))
                .thenReturn(Optional.of(parent));

        adminService.updateService(serviceId, dto);

        verify(homeServiceMapper)
                .updateEntityFromDto(dto, homeService);

        assertEquals(parent, homeService.getParent());

        verify(homeServiceRepository).save(homeService);
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionWhenServiceDoesnotExist() {
        Long serviceId = 1L;
        when(homeServiceRepository.findById(serviceId))
                .thenReturn(Optional.empty());
        EntityNotFoundException e =
                assertThrows(EntityNotFoundException.class, () -> adminService.deleteService(serviceId));
        assertEquals("HomeService not found with id: 1", e.getMessage());
        verify(homeServiceRepository).findById(serviceId);
        verify(homeServiceRepository, never()).deleteById(any());
    }

    @Test
    void shouldDeleteServiceSuccessfully() {

        Long serviceId = 1L;
        HomeService homeService = new HomeService();

        when(homeServiceRepository.findById(serviceId))
                .thenReturn(Optional.of(homeService));

        adminService.deleteService(serviceId);

        verify(homeServiceRepository).findById(serviceId);
        verify(homeServiceRepository).deleteById(serviceId);
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionWhenParentDoesNotExis() {

        Long parentId = 1L;

        when(homeServiceRepository.existsById(parentId))
                .thenReturn(false);

        EntityNotFoundException e =
                assertThrows(
                        EntityNotFoundException.class,
                        () -> adminService.getSubServices(parentId)
                );

        assertEquals(
                "Parent service not found with id: 1",
                e.getMessage()
        );

        verify(homeServiceRepository).existsById(parentId);
        verify(homeServiceRepository, never()).findByParentId(any());
    }

    @Test
    void shouldGetSubServicesSuccessfully() {

        Long parentId = 1L;

        HomeService service1 = new HomeService();
        HomeService service2 = new HomeService();

        HomeServiceResponseDto dto1 = mock(HomeServiceResponseDto.class);
        HomeServiceResponseDto dto2 = mock(HomeServiceResponseDto.class);

        when(homeServiceRepository.existsById(parentId))
                .thenReturn(true);

        when(homeServiceRepository.findByParentId(parentId))
                .thenReturn(List.of(service1, service2));

        when(homeServiceMapper.toResponseDto(service1))
                .thenReturn(dto1);

        when(homeServiceMapper.toResponseDto(service2))
                .thenReturn(dto2);

        List<HomeServiceResponseDto> result =
                adminService.getSubServices(parentId);

        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));

        verify(homeServiceRepository).existsById(parentId);
        verify(homeServiceRepository).findByParentId(parentId);

        verify(homeServiceMapper).toResponseDto(service1);
        verify(homeServiceMapper).toResponseDto(service2);
    }

    @Test
    public void shouldThrowDuplicateException() {
        SpecialistRequestDto dto = mock(SpecialistRequestDto.class);
        when(dto.email()).thenReturn("kianakhanlari");
        when(specialistRepository.existsByEmail("kianakhanlari"))
                .thenReturn(true);
        DuplicateException e =
                assertThrows(DuplicateException.class, () -> adminService.addSpecialist(dto));
        assertEquals("Email already exists", e.getMessage());
        verify(specialistRepository, never()).save(any());
    }

    @Test
    public void shouldAddSpecialistSuccessfully() {
        SpecialistRequestDto dto = mock(SpecialistRequestDto.class);

        when(dto.email()).thenReturn("kianakhanlari");
        when(specialistRepository.existsByEmail("kianakhanlari"))
                .thenReturn(false);

        Specialist specialist = new Specialist();

        when(specialistMapper.toEntity(dto))
                .thenReturn(specialist);

        adminService.addSpecialist(dto);

        assertEquals(
                SpecialistStatus.APPROVE,
                specialist.getStatus()
        );

        verify(specialistRepository).save(specialist);
    }

    @Test
    public void shouldThrowEntityNotFoundWhenSpecialistDoesnotExist() {
        Long specialistId = 1L;
        Long serviceId = 2L;
        when(specialistRepository.findById(1L))
                .thenReturn(Optional.empty());
        EntityNotFoundException e =
                assertThrows(EntityNotFoundException.class, () -> adminService.removeServiceFromSpecialist(specialistId, serviceId));
        assertEquals("there isnot Specialist with this id", e.getMessage());
        verify(homeServiceRepository, never()).findById(any());
        verify(specialistRepository, never()).save(any());
    }

    @Test
    public void shouldThrowEntityNotFoundWhenHomeServiceDoesNotExist() {

        Long specialistId = 1L;
        Long serviceId = 2L;

        Specialist specialist = new Specialist();

        when(specialistRepository.findById(specialistId))
                .thenReturn(Optional.of(specialist));

        when(homeServiceRepository.findById(serviceId))
                .thenReturn(Optional.empty());

        EntityNotFoundException e = assertThrows(
                EntityNotFoundException.class,
                () -> adminService.removeServiceFromSpecialist(
                        specialistId,
                        serviceId)
        );

        assertEquals(
                "there isnot homeservice with this id ",
                e.getMessage()
        );

        verify(specialistRepository).findById(specialistId);
        verify(homeServiceRepository).findById(serviceId);

    }


    @Test
    public void shouldThrowIllegalArgumentExceptionWhenSpecialistIsNotAssignedToService() {

        Long specialistId = 1L;
        Long serviceId = 2L;

        Specialist specialist = new Specialist();
        specialist.setServices(new HashSet<>());

        HomeService homeService = new HomeService();

        when(specialistRepository.findById(specialistId))
                .thenReturn(Optional.of(specialist));

        when(homeServiceRepository.findById(serviceId))
                .thenReturn(Optional.of(homeService));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> adminService.removeServiceFromSpecialist(
                                specialistId,
                                serviceId)
                );

        assertEquals(
                "This specialist is not assigned to the specified service",
                exception.getMessage()
        );

        verify(specialistRepository).findById(specialistId);
        verify(homeServiceRepository).findById(serviceId);

    }
    @Test
    public void shouldCallRemoveOnBothCollections() {

        Long specialistId = 1L;
        Long serviceId = 2L;

        Specialist specialist = mock(Specialist.class);
        HomeService homeService = mock(HomeService.class);

        Set<HomeService> services = mock(Set.class);
        Set<Specialist> specialists = mock(Set.class);

        when(specialistRepository.findById(specialistId))
                .thenReturn(Optional.of(specialist));

        when(homeServiceRepository.findById(serviceId))
                .thenReturn(Optional.of(homeService));

        when(specialist.getServices()).thenReturn(services);
        when(homeService.getSpecialists()).thenReturn(specialists);

        when(services.contains(homeService)).thenReturn(true);

        adminService.removeServiceFromSpecialist(specialistId, serviceId);

        verify(services).remove(homeService);
        verify(specialists).remove(specialist);
    }
    @Test
    public void shouldReturnWaitingAndNewSpecialists() {

        Pageable pageable = PageRequest.of(0, 10);

        Specialist specialist1 = new Specialist();
        Specialist specialist2 = new Specialist();

        SpecialistResponseDto dto1 = mock(SpecialistResponseDto.class);
        SpecialistResponseDto dto2 = mock(SpecialistResponseDto.class);

        Page<Specialist> specialistPage =
                new PageImpl<>(List.of(specialist1, specialist2));

        when(specialistRepository.findAllByStatusIn(
                List.of(SpecialistStatus.WAITING, SpecialistStatus.NEW),
                pageable))
                .thenReturn(specialistPage);

        when(specialistMapper.toResponseDto(specialist1))
                .thenReturn(dto1);

        when(specialistMapper.toResponseDto(specialist2))
                .thenReturn(dto2);

        Page<SpecialistResponseDto> result =
                adminService.getListWaitingAndNewSpecialists(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(dto1, result.getContent().get(0));
        assertEquals(dto2, result.getContent().get(1));

        verify(specialistRepository).findAllByStatusIn(
                List.of(SpecialistStatus.WAITING, SpecialistStatus.NEW),
                pageable);

        verify(specialistMapper).toResponseDto(specialist1);
        verify(specialistMapper).toResponseDto(specialist2);
    }
    @Test
    public void shouldThrowEntityNotFoundWhenSpecialistDoesNotExist() {

        Long specialistId = 1L;
        List<Long> serviceIds = List.of(1L, 2L);

        when(specialistRepository.findById(specialistId))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception =
                assertThrows(
                        EntityNotFoundException.class,
                        () -> adminService.approveSpecialist(
                                specialistId,
                                serviceIds)
                );

        assertEquals(
                "Specialist not found with id: 1",
                exception.getMessage()
        );

        verify(homeServiceRepository, never()).findAllById(any());
        verify(specialistRepository, never()).save(any());
    }
    @Test
    public void shouldThrowEntityNotFoundWhenOneOrMoreServicesDoNotExist() {

        Long specialistId = 1L;

        List<Long> serviceIds = List.of(1L, 2L);

        Specialist specialist = new Specialist();
        specialist.setServices(new HashSet<>());

        HomeService service = new HomeService();

        when(specialistRepository.findById(specialistId))
                .thenReturn(Optional.of(specialist));

        when(homeServiceRepository.findAllById(serviceIds))
                .thenReturn(List.of(service));

        EntityNotFoundException exception =
                assertThrows(
                        EntityNotFoundException.class,
                        () -> adminService.approveSpecialist(
                                specialistId,
                                serviceIds)
                );

        assertEquals(
                "One or more services not found",
                exception.getMessage()
        );

        verify(specialistRepository, never()).save(any());
    }
    @Test
    public void shouldApproveSpecialistSuccessfully() {

        Long specialistId = 1L;

        List<Long> serviceIds = List.of(1L, 2L);

        Specialist specialist = new Specialist();
        specialist.setServices(new HashSet<>());

        HomeService service1 = new HomeService();
        HomeService service2 = new HomeService();

        List<HomeService> services =
                List.of(service1, service2);

        when(specialistRepository.findById(specialistId))
                .thenReturn(Optional.of(specialist));

        when(homeServiceRepository.findAllById(serviceIds))
                .thenReturn(services);

        adminService.approveSpecialist(
                specialistId,
                serviceIds
        );

        assertEquals(
                SpecialistStatus.APPROVE,
                specialist.getStatus()
        );

        assertTrue(
                specialist.getServices().contains(service1)
        );

        assertTrue(
                specialist.getServices().contains(service2)
        );

        verify(specialistRepository).save(specialist);
    }
}