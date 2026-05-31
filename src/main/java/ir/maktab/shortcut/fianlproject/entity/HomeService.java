package ir.maktab.shortcut.fianlproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"parent_id", "name_service"})
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HomeService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  serviceId;

    private String nameService;

    private BigDecimal basePrice;

    private String description;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private HomeService parent;

    @OneToMany(mappedBy = "parent")
    private Set<HomeService> subServices = new HashSet<>();

    @OneToMany(mappedBy = "service")
    private Set<Specialist> specialistSets = new HashSet<>();

    @OneToMany(mappedBy = "service")
    private Set<Order> orders = new HashSet<>();

}
