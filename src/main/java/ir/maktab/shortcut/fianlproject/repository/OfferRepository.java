package ir.maktab.shortcut.fianlproject.repository;

import ir.maktab.shortcut.fianlproject.entity.Offer;
import ir.maktab.shortcut.fianlproject.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<Offer,Long> {
    boolean existsBySpecialist_IdAndOrder_StatusIn(
            Long specialistId,
            Collection<OrderStatus> statuses
    );


    List<Offer> findAllByOrder_IdOrderByProposedPriceAsc(Long orderId);

    List<Offer> findAllByOrder_IdOrderBySpecialistRateScoreDesc(Long orderId);
}
