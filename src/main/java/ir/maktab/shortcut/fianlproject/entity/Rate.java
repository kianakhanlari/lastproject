package ir.maktab.shortcut.fianlproject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Rate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(1) @Max(5)
    private int score;

    private String comment;

    private LocalDateTime createdAt;

    @OneToOne
    private Order order;
}
