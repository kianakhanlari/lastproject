package ir.maktab.shortcut.fianlproject.service;

import ir.maktab.shortcut.fianlproject.dtos.HomeServiceRequestDto;
import ir.maktab.shortcut.fianlproject.dtos.SpecialistRequestDto;
import ir.maktab.shortcut.fianlproject.dtos.response.HomeServiceResponseDto;
import ir.maktab.shortcut.fianlproject.dtos.response.SpecialistResponseDto;
import ir.maktab.shortcut.fianlproject.entity.HomeService;
import ir.maktab.shortcut.fianlproject.entity.Specialist;
import ir.maktab.shortcut.fianlproject.entity.enums.SpecialistStatus;
import ir.maktab.shortcut.fianlproject.exception.DuplicateException;
import ir.maktab.shortcut.fianlproject.mapper.HomeServiceMapper;
import ir.maktab.shortcut.fianlproject.mapper.SpecialistMapper;
import ir.maktab.shortcut.fianlproject.repository.HomeServiceRepository;
import ir.maktab.shortcut.fianlproject.repository.SpecialistRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public class AdminService{
    private final HomeServiceRepository homeServiceRepository;
    private final HomeServiceMapper homeServiceMapper;
    private final SpecialistRepository specialistRepository;
    private  final SpecialistMapper specialistMapper;

    public AdminService(HomeServiceRepository homeServiceRepository, HomeServiceMapper homeServiceMapper, SpecialistRepository specialistRepository, SpecialistMapper specialistMapper) {
        this.homeServiceRepository = homeServiceRepository;
        this.homeServiceMapper = homeServiceMapper;
        this.specialistRepository = specialistRepository;
        this.specialistMapper = specialistMapper;
    }

    public void createService(HomeServiceRequestDto dto) {

        HomeService parent = null;

        if (dto.parentId() != null) {
            parent = homeServiceRepository.findById(dto.parentId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Parent service not found with id: " + dto.parentId()
                    ));
        }

        HomeService homeService = homeServiceMapper.toEntity(dto);
        homeService.setParent(parent);
        homeServiceRepository.save(homeService);
    }

    public void updateService(Long serviceId, HomeServiceRequestDto dto) {
        HomeService homeService = homeServiceRepository.findById(serviceId)
                .orElseThrow();
        HomeService parent = null;
        if (dto.parentId() != null) {
            if (dto.parentId().equals(serviceId)) {
                throw new IllegalArgumentException("Service cannot be parent of itself");
            }
            parent = homeServiceRepository.findById(dto.parentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent service not found: " + dto.parentId()));

        }
        homeServiceMapper.updateEntityFromDto(dto, homeService);

        homeService.setParent(parent);

        homeServiceRepository.save(homeService);
    }
    public void deleteService(Long serviceId) {
            HomeService homeService = homeServiceRepository.findById(serviceId)
                    .orElseThrow(() -> new EntityNotFoundException("HomeService not found with id: " + serviceId));

        homeServiceRepository.deleteById(serviceId);
    }


    public List<HomeServiceResponseDto> getSubServices(Long parentId) {
        if (!homeServiceRepository.existsById(parentId)) {
            throw new EntityNotFoundException("Parent service not found with id: " + parentId);
        }

        List<HomeService> subServices = homeServiceRepository.findByParentId(parentId);

        return subServices.stream()
                .map(homeServiceMapper::toResponseDto)
                .toList();
    }

    public void addSpecialist(SpecialistRequestDto dto){
        if (specialistRepository.existsByEmail(dto.email())) {
            throw new DuplicateException();
        }
        Specialist specialist=specialistMapper.toEntity(dto);
        specialist.setStatus(SpecialistStatus.APPROVE);
        specialistRepository.save(specialist);
    }

    public void deleteSpecialist(Long specialistId) {
        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new EntityNotFoundException("Specialist not found with id: " + specialistId));

        specialistRepository.delete(specialist);
    }

    public Page<SpecialistResponseDto> getListWaitingAndNewSpecialists(Pageable pageable) {
        List<SpecialistStatus> statuses = List.of(SpecialistStatus.WAITING, SpecialistStatus.NEW);

        return specialistRepository.findAllByStatusIn(statuses, pageable)
                .map(specialistMapper::toResponseDto);
    }


    public void approveSpecialist(Long specialistId,Long serviceId){
        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new EntityNotFoundException("Specialist not found with id: " + specialistId));

        HomeService homeService = homeServiceRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("HomeService not found with id: " + serviceId));

       specialist.setStatus(SpecialistStatus.APPROVE);
       specialist.setService(homeService);
       specialistRepository.save(specialist);
    }

}
