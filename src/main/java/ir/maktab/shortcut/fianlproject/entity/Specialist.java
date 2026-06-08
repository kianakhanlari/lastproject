package ir.maktab.shortcut.fianlproject.entity;

import ir.maktab.shortcut.fianlproject.entity.enums.SpecialistStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Specialist extends Person {


    @ManyToMany
    @JoinTable(
            name = "specialist_service",
            joinColumns = @JoinColumn(name = "specialist_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private Set<HomeService> services = new HashSet<>();
     // @Lob
      @Column(name = "profile_image", columnDefinition = "bytea")
      private byte[] profileImage;

   @Enumerated(EnumType.STRING)
    private SpecialistStatus status;
    @OneToOne
    private Wallet wallet;
    @OneToMany(mappedBy = "specialist")
    private List<Offer> offers = new ArrayList<>();
    @OneToOne
    private Rate rate;
}
