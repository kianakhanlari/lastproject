package ir.maktab.shortcut.fianlproject.repository;

import ir.maktab.shortcut.fianlproject.entity.HomeService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HomeServiceRepository extends JpaRepository<HomeService,Long> {
    List<HomeService> findByParentIsNull();

    List<HomeService> findByParent_Id(Long parentId);

    List<HomeService> findByParentId(Long parentId);

    Optional<Object> findByNameServiceIgnoreCase(String name);
}
