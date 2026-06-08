package ir.maktab.shortcut.fianlproject.repository;

import ir.maktab.shortcut.fianlproject.entity.Specialist;
import ir.maktab.shortcut.fianlproject.entity.enums.SpecialistStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecialistRepository extends JpaRepository<Specialist, Long> {
    Page<Specialist> findAllByStatusIn(List<SpecialistStatus> statuses, Pageable pageable);

    boolean existsByEmail(String email);


    Specialist findByEmail(String email);
}
