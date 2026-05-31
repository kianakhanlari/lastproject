package ir.maktab.shortcut.fianlproject.service;

import ir.maktab.shortcut.fianlproject.dtos.OfferRequestDto;
import ir.maktab.shortcut.fianlproject.dtos.SpecialistRequestDto;
import ir.maktab.shortcut.fianlproject.dtos.response.SpecialistResponseDto;
import ir.maktab.shortcut.fianlproject.entity.Offer;
import ir.maktab.shortcut.fianlproject.entity.Order;
import ir.maktab.shortcut.fianlproject.entity.Specialist;
import ir.maktab.shortcut.fianlproject.entity.enums.OrderStatus;
import ir.maktab.shortcut.fianlproject.entity.enums.SpecialistStatus;
import ir.maktab.shortcut.fianlproject.exception.ActiveJobException;
import ir.maktab.shortcut.fianlproject.exception.NotApprovedException;
import ir.maktab.shortcut.fianlproject.mapper.OfferMapper;
import ir.maktab.shortcut.fianlproject.mapper.OrderMapper;
import ir.maktab.shortcut.fianlproject.mapper.SpecialistMapper;
import ir.maktab.shortcut.fianlproject.repository.OfferRepository;
import ir.maktab.shortcut.fianlproject.repository.OrderRepository;
import ir.maktab.shortcut.fianlproject.repository.SpecialistRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service

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

    public void registerSpecialist(SpecialistRequestDto dto, String filePath) {

        Specialist specialist = specialistMapper.toEntity(dto);
        specialist.setStatus(SpecialistStatus.NEW);
        specialist.setProfileImagePath(filePath);
        specialistRepository.save(specialist);
    }

    public void updateSpecialistProfile(SpecialistRequestDto dto) {

        Specialist specialist = specialistRepository.findById(dto.id())
                .orElseThrow(()
                        -> new EntityNotFoundException("Specialist with ID " + dto.id() + " not found"));
        if(orderRepository.existsBySpecialist_IdAndStatus(dto.id(), OrderStatus.ACCEPTED)){
            throw new ActiveJobException("You cannot update profile while you have an active job");
        }
        specialistMapper.updateEntityFromDto(dto, specialist);
        specialist.setStatus(SpecialistStatus.WAITING);
        specialistRepository.save(specialist);

    }

    public void createOffer(Long orderId, Long specialistId,OfferRequestDto dto) {
        Offer offer = offerMapper.toEntity(dto);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));



        if (order.getStatus() != OrderStatus.  WAITING_FOR_SPECIALIST) {
            throw new IllegalStateException("Order is not open for offers");
        }
        offer.setOrder(order);

        Specialist specialist=specialistRepository.findById(specialistId)
                .orElseThrow();

        if (specialist.getStatus() != SpecialistStatus.APPROVE) {
            throw new NotApprovedException();
        }
        offer.setSpecialist(specialist);

        offerRepository.save(offer);
    }


    public SpecialistResponseDto getProfile(Long personId) {
        Specialist specialist = specialistRepository.findById(personId)
                .orElseThrow(()
                        -> new EntityNotFoundException("person with ID " + personId + " not found"));
        SpecialistResponseDto dto = specialistMapper.toDto(specialist);
        return dto;
    }



}
