package ir.maktab.shortcut.fianlproject.repository;

import ir.maktab.shortcut.fianlproject.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferRepository extends JpaRepository<Offer,Long> {
}
