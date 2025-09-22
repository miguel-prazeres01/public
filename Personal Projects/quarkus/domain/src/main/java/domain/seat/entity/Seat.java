package domain.seat.entity;

import jakarta.persistence.ForeignKey;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import domain.base.entity.BaseEntity;
import domain.person.entity.Person;
import domain.show.entity.Show;
import io.smallrye.common.constraint.NotNull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Min;
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
        name = "seat",
        uniqueConstraints = { @UniqueConstraint(name = "uk_show_seat", columnNames = {"show_id", "row_letter", "seatNumber", "seatType"})}
)
@Entity
public class Seat extends BaseEntity{
    
    
    @NotBlank
    @Column(name = "row_letter")
    private String row;

    @NotNull
    @Min(1)
    @Column(name = "seatNumber")
    private Long seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeatStateEnum seatState;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, name="seatType")
    private SeatTypeEnum seatType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id",
                foreignKey = @ForeignKey(name = "FK_seat_person"))
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false,
                foreignKey = @ForeignKey(name = "FK_seat_show"))
    @JsonIgnore
    private Show show;

    @Column(name = "reservation_expires_at")
    private LocalDateTime reservationExpiresAt;

    @Column(name = "reserved_by_session", length = 100)
    private String reservedBySession;

    @Version
    private Long version;

    @Column(name = "transaction_id")
    private String transactionId;

}
