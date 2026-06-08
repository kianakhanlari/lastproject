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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional(readOnly = true)
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
    @Transactional
    public void createService(HomeServiceRequestDto dto) {

        HomeService parent = null;

        if (dto.parentId() != null) {
            parent = homeServiceRepository.findById(dto.parentId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Parent service not found with id: " + dto.parentId()
                    ));
        }


        if (homeServiceRepository.findByNameServiceIgnoreCase(dto.nameService()).isPresent()) {
            throw new HomeServiceAlreadyExistsException(
                    "HomeService with name '" + dto.nameService() + "' already exists"
            );
        }
        HomeService homeService = homeServiceMapper.toEntity(dto);
        homeService.setNameService(dto.nameService());
        homeService.setParent(parent);
        homeServiceRepository.save(homeService);
    }

   @Transactional
    public void updateService(Long serviceId, HomeServiceRequestDto dto) {
        HomeService homeService = homeServiceRepository.findById(serviceId)
                .orElseThrow(()->new EntityNotFoundException("there isnt HomeService With this id"));
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
        homeService.setNameService(dto.nameService());

        homeServiceRepository.save(homeService);
    }
    @Transactional
    public void deleteService(Long serviceId) {
            homeServiceRepository.findById(serviceId)
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
   @Transactional
    public void addSpecialist(SpecialistRequestDto dto){
        if (specialistRepository.existsByEmail(dto.email())) {
            throw new DuplicateException("Email already exists");
        }
        Specialist specialist=specialistMapper.toEntity(dto);
        specialist.setStatus(SpecialistStatus.APPROVE);
        specialistRepository.save(specialist);
    }
    @Transactional
    public void deleteSpecialist(Long specialistId) {
        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new EntityNotFoundException("Specialist not found with id: " + specialistId));

        specialistRepository.delete(specialist);
    }
    //رابطه بین متخصص و خدمت حذف کنه
    @Transactional
    public void removeServiceFromSpecialist(Long specialistId, Long serviceId){
        Specialist specialist=specialistRepository.findById(specialistId)
                .orElseThrow(()->new EntityNotFoundException("there isnot Specialist with this id"));
        HomeService homeService=homeServiceRepository.findById(serviceId)
                .orElseThrow(()->  new EntityNotFoundException("there isnot homeservice with this id "));

        if (!specialist.getServices().contains(homeService)) {
            throw new IllegalArgumentException(
                    "This specialist is not assigned to the specified service");
        }
        homeService.getSpecialists().remove(specialist);
        specialist.getServices().remove(homeService);

    }

    public Page<SpecialistResponseDto> getListWaitingAndNewSpecialists(Pageable pageable) {
        List<SpecialistStatus> statuses = List.of(SpecialistStatus.WAITING, SpecialistStatus.NEW);

        return specialistRepository.findAllByStatusIn(statuses, pageable)
                .map(specialistMapper::toResponseDto);
    }

   @Transactional
    public void approveSpecialist(Long specialistId, List<Long> serviceIds) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Specialist not found with id: " + specialistId));

        List<HomeService> services =
                homeServiceRepository.findAllById(serviceIds);

        if (services.size() != serviceIds.size()) {
            throw new EntityNotFoundException("One or more services not found");
        }

        specialist.setStatus(SpecialistStatus.APPROVE);

        specialist.getServices().addAll(services);

        specialistRepository.save(specialist);
    }


}
