package application.show.message.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import application.base.message.dto.BaseDTO;
import application.seat.message.dto.SeatDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ShowDTO extends BaseDTO{

    private String name;
    private LocalDateTime dateTime;
    private BigDecimal price;
    private List<SeatDTO> seats;

    public ShowDTO(Long id, String name, LocalDateTime dateTime, BigDecimal price, List<SeatDTO> seats){
        super(id);
        this.name = name;
        this.dateTime = dateTime;
        this.price = price;
        this.seats = seats;
    }
    
}
