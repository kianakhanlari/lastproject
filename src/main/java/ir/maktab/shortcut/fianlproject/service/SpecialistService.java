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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SpecialistService {
    private final SpecialistRepository specialistRepository;

    private final SpecialistMapper specialistMapper;

    private final OrderRepository orderRepository;


    private final OfferMapper offerMapper;

    private final OfferRepository offerRepository;

    public SpecialistService(SpecialistRepository specialistRepository, SpecialistMapper specialistMapper, OrderRepository orderRepository, OrderMapper orderMapper, OfferMapper offerMapper, OfferRepository offerRepository) {
        this.specialistRepository = specialistRepository;
        this.specialistMapper = specialistMapper;
        this.orderRepository = orderRepository;
        this.offerMapper = offerMapper;
        this.offerRepository = offerRepository;
    }
  /// register/update
   @Transactional
    public void registerSpecialist(SpecialistRequestDto dto) {

        if (specialistRepository.existsByEmail(dto.email())) {
            throw new DuplicateException("Email already exists");
        }
        Specialist specialist = specialistMapper.toEntity(dto);
        specialist.setStatus(SpecialistStatus.NEW);
        specialistRepository.save(specialist);
    }
  @Transactional
    public void updateSpecialistProfile(Long id,SpecialistRequestDto dto) {

        Specialist specialist = specialistRepository.findById(id)
                .orElseThrow(()
                        -> new EntityNotFoundException("Specialist with ID " + id + " not found"));
        List<OrderStatus> activeStatuses = List.of(
                OrderStatus.WAITING_FOR_SPECIALIST,
                OrderStatus.ACCEPTED
        );
        if(offerRepository.existsBySpecialist_IdAndOrder_StatusIn(id, activeStatuses)){
            throw new ActiveJobException("You cannot update profile while you have an active job");
        }

        specialistMapper.updateEntityFromDto(dto, specialist);
        specialist.setStatus(SpecialistStatus.WAITING);

    }
     @Transactional
    public void createOffer(Long orderId, Long specialistId,OfferDto dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

        Offer offer = offerMapper.toEntity(dto);

        if (order.getStatus() != OrderStatus.  WAITING_FOR_OFFERS) {
            throw new IllegalStateException("Order is not open for offers");
        }
        offer.setOrder(order);

         Specialist specialist = specialistRepository.findById(specialistId)
                 .orElseThrow(() ->
                         new EntityNotFoundException(
                                 "Specialist not found with id: " + specialistId));

        if (specialist.getStatus() != SpecialistStatus.APPROVE) {
            throw new NotApprovedException();
        }
        offer.setSpecialist(specialist);


        offerRepository.save(offer);
    }


}
