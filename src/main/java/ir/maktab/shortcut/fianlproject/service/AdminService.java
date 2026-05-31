package ir.maktab.shortcut.fianlproject.service;

import ir.maktab.shortcut.fianlproject.dtos.HomeServiceRequestDto;
import ir.maktab.shortcut.fianlproject.dtos.SpecialistRequestDto;
import ir.maktab.shortcut.fianlproject.dtos.response.SpecialistResponseDto;
import ir.maktab.shortcut.fianlproject.entity.HomeService;
import ir.maktab.shortcut.fianlproject.entity.Specialist;
import ir.maktab.shortcut.fianlproject.entity.enums.SpecialistStatus;
import ir.maktab.shortcut.fianlproject.mapper.HomeServiceMapper;
import ir.maktab.shortcut.fianlproject.mapper.SpecialistMapper;
import ir.maktab.shortcut.fianlproject.repository.HomeServiceRepository;
import ir.maktab.shortcut.fianlproject.repository.SpecialistRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

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

    public void createService(HomeServiceRequestDto dto){
        HomeService homeService=homeServiceMapper.toEntity(dto);
        homeServiceRepository.save(homeService);
    }
    public void updateService(Long serviceId, HomeServiceRequestDto dto){
        HomeService homeService = homeServiceRepository.findById(serviceId)
                .orElseThrow();

        homeServiceMapper.updateEntityFromDto(dto, homeService);
        homeServiceRepository.save(homeService);
    }
    public void deleteService(Long serviceId) {
        if (!homeServiceRepository.existsById(serviceId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "HomeService not found with id: " + serviceId
            );
        }
        homeServiceRepository.deleteById(serviceId);
    }
    public Optional<HomeService> getServiceById(Long serviceId) {
        return homeServiceRepository.findById(serviceId);
    }

    public List<HomeService> getAllServices(){
        return homeServiceRepository.findAll();
    }

    public void addSpecialist(SpecialistRequestDto dto){
        Specialist specialist=specialistMapper.toEntity(dto);
        specialistRepository.save(specialist);
    }
    public void deleteSpecialist(Long specialistId){
        if(!specialistRepository.existsById(specialistId)){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "HomeService not found with id: " + specialistId
            );
        }

        specialistRepository.findById(specialistId);
    }
    public List<SpecialistResponseDto> getListWaitingAndNewSpecialists() {

        List<Specialist> specialists =
                specialistRepository.findAllByAccountStatusIn(
                        List.of(SpecialistStatus.WAITING, SpecialistStatus.NEW)
                );

        return specialists.stream()
                .map(specialistMapper::toResponseDto)
                .toList();
    }

    public void approveSpecialist(Long specialistId){
       Specialist specialist=specialistRepository.findById(specialistId)
               .orElseThrow();
       specialist.setStatus(SpecialistStatus.APPROVE);
       specialistRepository.save(specialist);
    }

}
