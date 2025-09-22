package domain.show.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import domain.base.entity.BaseEntity;
import domain.seat.entity.Seat;
import io.smallrye.common.constraint.NotNull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "shows", uniqueConstraints = { @UniqueConstraint(name = "uk_show_name", columnNames = "name")}
)
@Entity
public class Show extends BaseEntity{
    
    @NotBlank
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "dateTime")
    private LocalDateTime dateTime;

    @NotNull
    @DecimalMin("0.01")
    @Column(name= "price", nullable = false)
    private BigDecimal price;

    @OneToMany(
        mappedBy = "show",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY,       
        orphanRemoval = true
    )    
    @JsonIgnore
    private List<Seat> seats;    
}
