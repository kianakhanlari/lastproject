package ir.maktab.shortcut.fianlproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal priceOffer;
    private LocalDateTime appointmentTime;
    @Column(columnDefinition = "interval", nullable = false)
    private Duration workDuration;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;



    @ManyToOne
    private Order order;

    @ManyToOne
    private Specialist specialist;
}
