package ir.maktab.shortcut.fianlproject.entity;

import ir.maktab.shortcut.fianlproject.entity.enums.SpecialistStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Specialist extends Person {


    @ManyToOne
    private HomeService service;
    /*  @Lob
      @Column(columnDefinition = "BYTEA")
      private byte[] profileImage;*/
    private String profileImagePath;
    private SpecialistStatus Status;
    @OneToOne
    private Wallet wallet;
    @OneToMany
    private Set<Offer> offer = new HashSet<>();
    @OneToMany(mappedBy = "specialist")
    private List<Offer> offers;
}
