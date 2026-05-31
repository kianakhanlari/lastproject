package ir.maktab.shortcut.fianlproject.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
@EntityListeners(AuditingEntityListener.class)

@MappedSuperclass
@Setter
@Getter
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    @Column(nullable = false, unique = true)
    private String email;
    private String password;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
