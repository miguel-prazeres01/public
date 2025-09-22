package application.show.mapper;

import java.util.Comparator;
import java.util.List;

import application.base.converter.Mapper;
import application.seat.mapper.SeatMapper;
import application.seat.message.dto.SeatDTO;
import application.show.message.dto.ShowDTO;
import domain.seat.entity.SeatTypeEnum;
import domain.show.entity.Show;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ShowMapper implements Mapper<Show,ShowDTO>{
    
    private SeatMapper seatMapper;

    @Inject
    public ShowMapper(SeatMapper seatMapper){
        this.seatMapper = seatMapper;
    }

    public ShowDTO toDto(Show show){
        List<SeatDTO> seatDTOs = seatMapper.toDto(show.getSeats());

        seatDTOs.sort(Comparator
        .comparing((SeatDTO s) -> s.getSeatType(), Comparator.comparingInt(seatType -> {
            if (seatType == SeatTypeEnum.PLATEIA) return 0;
            if (seatType == SeatTypeEnum.BALCAO) return 1;
            return 2;
        }))
        .thenComparing(SeatDTO::getRow)
        .thenComparing(SeatDTO::getSeatNumber, Comparator.reverseOrder())
    );

        return new ShowDTO(show.getId(), show.getName(), show.getDateTime(), show.getPrice(), seatDTOs);
    }
}
