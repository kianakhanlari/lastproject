package ir.maktab.shortcut.fianlproject.repository;

import ir.maktab.shortcut.fianlproject.entity.Specialist;
import ir.maktab.shortcut.fianlproject.entity.enums.SpecialistStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SpecialistRepository extends JpaRepository<Specialist,Long> {
    List<Specialist> findAllByAccountStatusIn(Collection<SpecialistStatus> statuses);
}
