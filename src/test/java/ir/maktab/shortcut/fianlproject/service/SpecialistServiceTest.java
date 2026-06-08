package ir.maktab.shortcut.fianlproject.service;

import ir.maktab.shortcut.fianlproject.dtos.OfferDto;
import ir.maktab.shortcut.fianlproject.dtos.SpecialistRequestDto;
import ir.maktab.shortcut.fianlproject.entity.Offer;
import ir.maktab.shortcut.fianlproject.entity.Order;
import ir.maktab.shortcut.fianlproject.entity.Specialist;
import ir.maktab.shortcut.fianlproject.entity.enums.OrderStatus;
import ir.maktab.shortcut.fianlproject.entity.enums.SpecialistStatus;
import ir.maktab.shortcut.fianlproject.exception.ActiveJobException;
import ir.maktab.shortcut.fianlproject.exception.DuplicateException;
import ir.maktab.shortcut.fianlproject.exception.NotApprovedException;
import ir.maktab.shortcut.fianlproject.mapper.OfferMapper;
import ir.maktab.shortcut.fianlproject.mapper.OrderMapper;
import ir.maktab.shortcut.fianlproject.mapper.SpecialistMapper;
import ir.maktab.shortcut.fianlproject.repository.OfferRepository;
import ir.maktab.shortcut.fianlproject.repository.OrderRepository;
import ir.maktab.shortcut.fianlproject.repository.SpecialistRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.DigestException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialistServiceTest {
    @Mock
    private SpecialistRepository specialistRepository;
    @Mock
    private SpecialistMapper specialistMapper;
    @Mock
    private OfferRepository offerRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OfferMapper offerMapper;
    @InjectMocks
    private SpecialistService specialistService;


    @Test
    void shouldThrowDuplicateExceptionWhenEmailAlreadyExist() {
        SpecialistRequestDto dto = mock(SpecialistRequestDto.class);
        when(dto.email()).thenReturn("kianakhanlari");
        when(specialistRepository.existsByEmail("kianakhanlari"))
                .thenReturn(true);

        DuplicateException e =
                assertThrows(DuplicateException.class, () -> specialistService.registerSpecialist(dto));
        assertEquals("Email already exists", e.getMessage());
        verify(specialistRepository).existsByEmail("kianakhanlari");
        verify(specialistRepository, never()).save(any());
    }

    @Test
    void shouldRegisterSpecialistSuccessfully() {

        SpecialistRequestDto dto = mock(SpecialistRequestDto.class);

        when(dto.email()).thenReturn("kianakhanlari");

        when(specialistRepository.existsByEmail("kianakhanlari"))
                .thenReturn(false);

        Specialist specialist = new Specialist();

        when(specialistMapper.toEntity(dto))
                .thenReturn(specialist);

        specialistService.registerSpecialist(dto);

        assertEquals(
                SpecialistStatus.NEW,
                specialist.getStatus()
        );

        verify(specialistRepository)
                .save(specialist);
    }

    @Test
    void souldThrowEntityNotFoundExceptionWhenSpecialistDoesNotExist() {
        Long id = 1L;
        SpecialistRequestDto dto = mock(SpecialistRequestDto.class);

        when(specialistRepository.findById(1L))
                .thenReturn(Optional.empty());
        EntityNotFoundException e =
                assertThrows(EntityNotFoundException.class, () -> specialistService.updateSpecialistProfile(id, dto));
        assertEquals("Specialist with ID 1 not found", e.getMessage());
        verify(specialistRepository).findById(id);
        verify(offerRepository, never())
                .existsBySpecialist_IdAndOrder_StatusIn(anyLong(), anyList());
        verify(specialistRepository, never()).save(any());
    }

    @Test
    void shouldThrowActiveJobExceptionWhenSpecialistHasACtiveJob() {
        Long id = 1L;
        SpecialistRequestDto dto = mock(SpecialistRequestDto.class);
        Specialist specialist = new Specialist();
        when(specialistRepository.findById(1L))
                .thenReturn(Optional.of(specialist));
        List<OrderStatus> activeStatuses = List.of(
                OrderStatus.WAITING_FOR_SPECIALIST,
                OrderStatus.ACCEPTED
        );
        when(offerRepository.existsBySpecialist_IdAndOrder_StatusIn(id, activeStatuses))
                .thenReturn(true);
        ActiveJobException e =
                assertThrows(ActiveJobException.class, () -> specialistService.updateSpecialistProfile(id, dto));
        assertEquals("You cannot update profile while you have an active job", e.getMessage());
        verify(specialistRepository).findById(id);
        verify(offerRepository)
                .existsBySpecialist_IdAndOrder_StatusIn(id, activeStatuses);
        verify(specialistMapper, never())
                .updateEntityFromDto(any(), any());
        verify(specialistRepository, never()).save(any());
    }

    @Test
    void shouldUpdateSpecialistProfileSuccessfully() {

        Long id = 1L;
        SpecialistRequestDto dto = mock(SpecialistRequestDto.class);

        Specialist specialist = new Specialist();

        when(specialistRepository.findById(id))
                .thenReturn(Optional.of(specialist));

        List<OrderStatus> activeStatuses = List.of(
                OrderStatus.WAITING_FOR_SPECIALIST,
                OrderStatus.ACCEPTED
        );

        when(offerRepository.existsBySpecialist_IdAndOrder_StatusIn(id, activeStatuses))
                .thenReturn(false);

        doNothing().when(specialistMapper)
                .updateEntityFromDto(dto, specialist);

        specialistService.updateSpecialistProfile(id, dto);

        verify(specialistMapper)
                .updateEntityFromDto(dto, specialist);

        verify(specialistRepository)
                .save(specialist);

        assertEquals(
                SpecialistStatus.WAITING,
                specialist.getStatus()
        );
    }


    @Test
    void shouldThrowEntityNotFoundExceptionWhenOrderDoesNotExist() {

        Long orderId = 1L;
        Long specialistId = 2L;

        OfferDto dto = mock(OfferDto.class);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> specialistService.createOffer(orderId, specialistId, dto)
        );

        assertEquals(
                "Order not found with id: 1",
                exception.getMessage()
        );

        verify(offerRepository, never()).save(any());
        verify(specialistRepository, never()).findById(any());
    }

    @Test
    void shouldThrowExceptionWhenSpecialistIsNotApproved() {

        Long orderId = 1L;
        Long specialistId = 2L;

        OfferDto dto = mock(OfferDto.class);

        Order order = new Order();
        order.setStatus(OrderStatus.WAITING_FOR_SPECIALIST);

        Specialist specialist = new Specialist();
        specialist.setStatus(SpecialistStatus.WAITING);

        Offer offer = new Offer();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(specialistRepository.findById(specialistId))
                .thenReturn(Optional.of(specialist));

        when(offerMapper.toEntity(dto))
                .thenReturn(offer);

        assertThrows(
                NotApprovedException.class,
                () -> specialistService.createOffer(orderId, specialistId, dto)
        );

        verify(offerRepository, never()).save(any());
    }
    @Test
    void shouldThrowExceptionWhenOrderIsNotWaitingForSpecialist() {

        Long orderId = 1L;
        Long specialistId = 2L;

        OfferDto dto = mock(OfferDto.class);

        Order order = new Order();
        order.setStatus(OrderStatus.ACCEPTED);

        Offer offer = new Offer();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(offerMapper.toEntity(dto))
                .thenReturn(offer);

        assertThrows(
                IllegalStateException.class,
                () -> specialistService.createOffer(orderId, specialistId, dto)
        );

        verify(offerRepository, never()).save(any());
    }
    @Test
    void shouldCreateOfferSuccessfully() {

        Long orderId = 1L;
        Long specialistId = 2L;

        OfferDto dto = mock(OfferDto.class);

        Order order = new Order();
        order.setStatus(OrderStatus.WAITING_FOR_SPECIALIST);

        Specialist specialist = new Specialist();
        specialist.setStatus(SpecialistStatus.APPROVE);

        Offer offer = new Offer();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(specialistRepository.findById(specialistId))
                .thenReturn(Optional.of(specialist));

        when(offerMapper.toEntity(dto))
                .thenReturn(offer);

        specialistService.createOffer(orderId, specialistId, dto);

        assertEquals(order, offer.getOrder());
        assertEquals(specialist, offer.getSpecialist());

        verify(offerRepository).save(offer);
    }
}