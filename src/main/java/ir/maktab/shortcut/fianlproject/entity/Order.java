package ir.maktab.shortcut.fianlproject.entity;

import ir.maktab.shortcut.fianlproject.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //status

    private String description;

    private BigDecimal proposedPrice;

    private LocalDateTime appointmentTime;

    private String address;

    @CreatedDate
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    private Customer customer;



    @OneToMany(mappedBy = "order")
    private Set<Offer> offers;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private HomeService service;

}
